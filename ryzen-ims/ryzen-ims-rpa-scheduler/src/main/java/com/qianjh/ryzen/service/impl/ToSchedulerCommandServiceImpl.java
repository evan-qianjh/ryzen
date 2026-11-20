package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.enums.ExecutorAssignmentStatus;
import com.qianjh.ryzen.enums.ExecutorCommandType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.mapper.ExecutorAssignmentMapper;
import com.qianjh.ryzen.model.ExecutorCommandRequest;
import com.qianjh.ryzen.model.SchedulerCommandRequest;
import com.qianjh.ryzen.model.SchedulerCommandResponse;
import com.qianjh.ryzen.service.ExecutorAssignmentService;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.ToSchedulerCommandService;
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
public class ToSchedulerCommandServiceImpl implements ToSchedulerCommandService {

    private final ExecutorService executorService;
    private final KafkaSchedulerTransport transport;
    private final ExecutorAssignmentMapper assignmentMapper;
    private final ExecutorAssignmentService executorAssignmentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void handle(SchedulerCommandRequest request) {
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
            transport.schedulerCommandResponse(
                    SchedulerCommandResponse.builder()
                            .code(request.code())
                            .type(request.type())
                            .imType(request.imType())
                            .accountCode(request.accountCode())
                            .assignmentId(request.assignmentId())
                            //
                            .success(false)
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    /**
     * 启动
     *
     * @param request 请求
     */
    void start(SchedulerCommandRequest request) {
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
                    .assignment(com.qianjh.ryzen.model.Assignment.builder()
                            .id(assignment.getId())
                            .imType(assignment.getImType())
                            .accountCode(assignment.getAccountCode())
                            .build())
                    .build()
            ).get(10, TimeUnit.SECONDS);

            // 调度器指令响应
            transport.schedulerCommandResponse(
                    SchedulerCommandResponse.builder()
                            .code(request.code())
                            .type(request.type())
                            .assignmentId(assignment.getId())
                            .success(true)
                            .build()
            ).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 停止
     *
     * @param request 请求
     */
    void stop(SchedulerCommandRequest request) {
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
            transport.schedulerCommandResponse(
                    SchedulerCommandResponse.builder()
                            .code(request.code())
                            .type(request.type())
                            .assignmentId(assignment.getId())
                            .success(true)
                            .build()
            ).get(10, TimeUnit.SECONDS);
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
    void assign(SchedulerCommandRequest request) {
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

        try {
            // 调度器指令响应
            transport.schedulerCommandResponse(
                    SchedulerCommandResponse.builder()
                            .code(request.code())
                            .type(request.type())
                            .imType(request.imType())
                            .accountCode(request.accountCode())
                            .assignmentId(assignment.getId())
                            .success(success)
                            .build()
            ).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 释放
     *
     * @param request 请求
     */
    void release(SchedulerCommandRequest request) {
        ExecutorAssignment assignment;
        if (request.assignmentId() != null) { // 精确
            assignment = executorAssignmentService.getById(request.assignmentId());
            if (assignment == null) {
                log.error("获取执行器分配失败 ::: {}", request);
                return;
            }
        } else { // 兜底
            assignment = executorAssignmentService.getOne(new LambdaQueryWrapper<ExecutorAssignment>()
                    .eq(ExecutorAssignment::getImType, request.imType())
                    .eq(ExecutorAssignment::getAccountCode, request.accountCode())
                    .orderByDesc(ExecutorAssignment::getId)
                    .last(" limit 1")
            );
        }

        Executor executor = executorService.getById(assignment.getExecutorId());
        if (executor == null) {
            log.error("获取执行器失败 ::: {}", request);
            return;
        }

        // 释放
        // TODO 不严谨，应该放在command回调中执行
        executorAssignmentService.updateStatus(assignment, assignment.getStatus(), ExecutorAssignmentStatus.RELEASED, Operator.builder().build());
        executorService.release(executor, assignment);

        try {
            // 调度器指令响应
            transport.schedulerCommandResponse(
                    SchedulerCommandResponse.builder()
                            .code(request.code())
                            .type(request.type())
                            .imType(request.imType())
                            .accountCode(request.accountCode())
                            .assignmentId(request.assignmentId())
                            .build()
            ).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }


}
