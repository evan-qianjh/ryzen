package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.enums.ExecutorCommandType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.mapper.ExecutorAssignmentMapper;
import com.qianjh.ryzen.model.ExecutorCommand;
import com.qianjh.ryzen.model.SchedulerCommand;
import com.qianjh.ryzen.service.ExecutorAssignmentService;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.SchedulerCommandService;
import com.qianjh.ryzen.transport.KafkaSchedulerTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final ExecutorAssignmentService assignmentService;

    @Override
    public void handle(SchedulerCommand command) {
        switch (command.type()) {
            // 启动
            case ASSIGN -> {
                // 查出最近分配过且IDLE的executor
                ExecutorAssignment lastAssignment = assignmentMapper.selectJoinOne(new MPJLambdaWrapper<ExecutorAssignment>()
                        .selectAll(ExecutorAssignment.class)
                        .innerJoin(Executor.class, Executor::getId, ExecutorAssignment::getExecutorId)
                        .eq(ExecutorAssignment::getImType, command.imType())
                        .eq(ExecutorAssignment::getAccountCode, command.accountCode())
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
                            .orderByAsc(Executor::getRegisteredTime)
                    );
                }
                if (executor == null) {
                    log.error("获取执行器失败 ::: {}", command);
                    return;
                }

                // 创建新的分配
                ExecutorAssignment assignment = assignmentService.create(executor, command.imType(), command.accountCode());

                // build
                ExecutorCommand executorCommand = ExecutorCommand.builder()
                        .code(IdUtils.generateUUID())
                        .type(ExecutorCommandType.START)
                        .executorCode(executor.getCode())
                        .assignment(com.qianjh.ryzen.model.Assignment.builder()
                                .id(assignment.getId())
                                .fencingToken(null)
                                .imType(assignment.getImType())
                                .accountCode(assignment.getAccountCode())
                                .build())
                        .build();
                try {
                    transport.executorCommand(executorCommand).get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
            // 停止
            case RELEASE -> {
//                ExecutorAssignment assignment = assignmentService.getById(command.assignmentId());
//                if(assignment == null) {
//                    log.error("获取执行器分配失败 ::: {}", command);
//                    return;
//                }
                // TODO 不严谨，临时用下，应该改成上面通过assignmentId来停止
                ExecutorAssignment assignment = assignmentService.getOne(new LambdaQueryWrapper<ExecutorAssignment>()
                        .eq(ExecutorAssignment::getImType, command.imType())
                        .eq(ExecutorAssignment::getAccountCode, command.accountCode())
                        .orderByDesc(ExecutorAssignment::getId)
                        .last(" limit 1")
                );

                Executor executor = executorService.getById(assignment.getExecutorId());
                if (executor == null) {
                    log.error("获取执行器失败 ::: {}", command);
                    return;
                }

                ExecutorCommand executorCommand = ExecutorCommand.builder()
                        .code(IdUtils.generateUUID())
                        .type(ExecutorCommandType.STOP)
                        .executorCode(executor.getCode())
                        .build();
                try {
                    transport.executorCommand(executorCommand).get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
