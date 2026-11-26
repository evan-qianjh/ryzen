package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.enums.ExecutorAssignmentStatus;
import com.qianjh.ryzen.ims.enums.ExecutorCommandType;
import com.qianjh.ryzen.ims.enums.ExecutorStatus;
import com.qianjh.ryzen.ims.dto.Assignment;
import com.qianjh.ryzen.ims.dto.ExecutorCommandRequest;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.command.type.AppToSchedulerCommandRequest;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.command.type.SchedulerToAppCommandResponse;
import com.qianjh.ryzen.mapper.ExecutorAssignmentMapper;
import com.qianjh.ryzen.service.ExecutorAssignmentService;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.SchedulerCommandService;
import com.qianjh.ryzen.transport.KafkaSchedulerTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerCommandServiceImpl implements SchedulerCommandService {

    private final ExecutorService executorService;
    private final KafkaSchedulerTransport transport;
    private final ExecutorAssignmentMapper assignmentMapper;
    private final ExecutorAssignmentService executorAssignmentService;
    private final SchedulerToAppCommandResponse.Publisher schedulerToAppCommandResponsePublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(AppToSchedulerCommandRequest.Body request) {
        try {
            switch (request.type()) {
                // 分配
                case ASSIGN_EXECUTOR -> assign(request);
                // 释放
                case RELEASE_EXECUTOR -> release(request);
                // 启动
                case START_EXECUTOR -> start(request);
                // 停止
                case STOP_EXECUTOR -> stop(request);
            }
        } catch (Exception e) {
            log.error("处理指令失败 ::: request={}", request, e);
            schedulerToAppCommandResponsePublisher.publish(
                    SchedulerToAppCommandResponse.Body.builder()
                            .code(request.code())
                            .type(request.type())
                            .imType(request.imType())
                            .accountCode(request.accountCode())
                            .assignmentId(request.assignmentId())
                            //
                            .success(false)
                            .errorMessage(e.getMessage())
                            .build()
            );
        }
    }


    /**
     * 启动
     *
     * @param request 请求
     */
    @Transactional(rollbackFor = Exception.class)
    void start(AppToSchedulerCommandRequest.Body request) {
        ExecutorAssignment assignment = executorAssignmentService.getById(request.assignmentId());
        if (assignment == null) {
            throw new RuntimeException("指派不存在");
        }
        if (assignment.getStatus() != ExecutorAssignmentStatus.ASSIGNED) {
            throw new RuntimeException("指派状态异常");
        }
        Executor executor = executorService.getById(assignment.getExecutorId());
        if (executor == null) {
            throw new RuntimeException("执行器不存在");
        }
        if (!executor.getCurrentAssigmentId().equals(assignment.getId())) {
            throw new RuntimeException("执行器绑定关系异常");
        }

        try {
            // 执行器指令请求
            transport.executorCommandRequest(ExecutorCommandRequest.builder()
                    .code(IdUtils.generateUUID())
                    .type(ExecutorCommandType.START)
                    .executorCode(executor.getCode())
                    .assignment(Assignment.builder()
                            .id(assignment.getId())
                            .imType(assignment.getImType())
                            .accountCode(assignment.getAccountCode())
                            .build())
                    .build()
            ).get(10, TimeUnit.SECONDS);

            // 调度器指令响应
            schedulerToAppCommandResponsePublisher.publish(
                    SchedulerToAppCommandResponse.Body.builder()
                            .code(request.code())
                            .type(request.type())
                            .assignmentId(assignment.getId())
                            .success(true)
                            .build()
            );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止
     *
     * @param request 请求
     */
    @Transactional(rollbackFor = Exception.class)
    void stop(AppToSchedulerCommandRequest.Body request) {
        ExecutorAssignment assignment = executorAssignmentService.getById(request.assignmentId());
        if (assignment == null) {
            throw new RuntimeException("指派不存在");
        }

        Executor executor = executorService.getById(assignment.getExecutorId());
        if (executor == null) {
            throw new RuntimeException("执行器不存在");
        }

        try {
            // 执行器指令请求
            transport.executorCommandRequest(
                    ExecutorCommandRequest.builder()
                            .code(IdUtils.generateUUID())
                            .type(ExecutorCommandType.STOP)
                            .executorCode(executor.getCode())
                            .build()
            ).get(10, TimeUnit.SECONDS);

            // 调度器指令响应
            schedulerToAppCommandResponsePublisher.publish(
                    SchedulerToAppCommandResponse.Body.builder()
                            .code(request.code())
                            .type(request.type())
                            .assignmentId(assignment.getId())
                            .success(true)
                            .build()
            );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分配
     *
     * @param request 请求
     */
    @Transactional(rollbackFor = Exception.class)
    void assign(AppToSchedulerCommandRequest.Body request) {
        // 查出是否存在还未关闭的assignment
        long count = executorAssignmentService.count(new LambdaQueryWrapper<ExecutorAssignment>()
                .eq(ExecutorAssignment::getImType, request.imType())
                .eq(ExecutorAssignment::getAccountCode, request.accountCode())
                .eq(ExecutorAssignment::getStatus, ExecutorAssignmentStatus.ASSIGNED)
        );
        if (count > 0) {
            throw new RuntimeException("存在未关闭的指派");
        }

        // 查出最近分配过且IDLE的executor
        ExecutorAssignment lastAssignment = assignmentMapper.selectJoinOne(new MPJLambdaWrapper<ExecutorAssignment>()
                .selectAll(ExecutorAssignment.class)
                .innerJoin(Executor.class, Executor::getId, ExecutorAssignment::getExecutorId)
                .eq(ExecutorAssignment::getImType, request.imType())
                .eq(ExecutorAssignment::getAccountCode, request.accountCode())
                .eq(Executor::getStatus, ExecutorStatus.IDLE)
                .orderByDesc(ExecutorAssignment::getId)
                .last(" limit 1")
        );
        Executor executor;
        if (lastAssignment != null) {
            executor = executorService.getById(lastAssignment.getExecutorId());
        } else { // 如果没有则获取最早注册IDLE
            executor = executorService.getOne(new LambdaQueryWrapper<Executor>()
                    .eq(Executor::getStatus, ExecutorStatus.IDLE)
                    .isNull(Executor::getCurrentAssigmentId)
                    .orderByAsc(Executor::getRegisteredTime)
            );
        }
        if (executor == null) {
            throw new RuntimeException("没有可用的执行器");
        }
        if (executor.getCurrentAssigmentId() != null) {
            throw new RuntimeException("分配的执行器已被占用");
        }

        // 创建新的分配
        ExecutorAssignment assignment = executorAssignmentService.create(executor, request.imType(), request.accountCode());
        boolean success = executorService.assign(executor, assignment);

        // 调度器指令响应
        schedulerToAppCommandResponsePublisher.publish(
                SchedulerToAppCommandResponse.Body.builder()
                        .code(request.code())
                        .type(request.type())
                        .imType(request.imType())
                        .accountCode(request.accountCode())
                        .assignmentId(assignment.getId())
                        .success(success)
                        .build()
        );

    }

    /**
     * 释放
     *
     * @param request 请求
     */
    @Transactional(rollbackFor = Exception.class)
    void release(AppToSchedulerCommandRequest.Body request) throws RuntimeException {
        if (request.assignmentId() == null) {
            throw new RuntimeException("缺少参数assignmentId");
        }

        ExecutorAssignment assignment = executorAssignmentService.getById(request.assignmentId());
        if (assignment == null) {
            throw new RuntimeException("获取执行器分配失败");
        }

        Executor executor = executorService.getById(assignment.getExecutorId());
        if (executor == null) {
            throw new RuntimeException("获取执行器失败");
        }

        // 释放
        executorAssignmentService.updateStatus(assignment, assignment.getStatus(), ExecutorAssignmentStatus.RELEASED, Operator.builder().build());
        executorService.release(executor, assignment);

        schedulerToAppCommandResponsePublisher.publish(
                SchedulerToAppCommandResponse.Body.builder()
                        .code(request.code())
                        .type(request.type())
                        .assignmentId(request.assignmentId())
                        .success(true)
                        .build()
        );
    }

}
