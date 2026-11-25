package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.ims.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.mapper.ExecutorTaskMapper;
import com.qianjh.ryzen.ims.model.ExecutorTaskResponse;
import com.qianjh.ryzen.service.ExecutorTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecutorTaskServiceImpl extends ServiceImpl<ExecutorTaskMapper, ExecutorTask> implements ExecutorTaskService {

    @Override
    public ExecutorTask getByUk(String commandCode) {
        return getOne(new LambdaQueryWrapper<ExecutorTask>()
                .eq(ExecutorTask::getCommandCode, commandCode)
        );
    }

    @Override
    public ExecutorTask getByCommandCode(String commandCode) {
        return getOne(new LambdaQueryWrapper<ExecutorTask>()
                .eq(ExecutorTask::getCommandCode, commandCode)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ExecutorTask create(String commandCode, Long assignmentId, String conversationCode, String content, LocalDateTime deadlineTime) {
        ExecutorTask entity = ExecutorTask.builder()
                .commandCode(commandCode)
                .assignmentId(assignmentId)
//                .imType(imType)
//                .accountCode(accountCode)
                .conversationCode(conversationCode)
                .content(content)
                .status(ExecutorTaskStatus.PENDING)
                .createdTime(LocalDateTime.now())
                .deadlineTime(deadlineTime)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ExecutorTask createIfAbsent(String commandCode, Long assignmentId, String conversationCode, String content, LocalDateTime deadlineTime) {
        ExecutorTask entity = getByUk(commandCode);
        if (entity != null) {
            return entity;
        }
        return create(commandCode, assignmentId, conversationCode, content, deadlineTime);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean timeoutPendingTasks(LocalDateTime now) {
        return lambdaUpdate()
                .set(ExecutorTask::getStatus, ExecutorTaskStatus.TIMEOUT)
                .set(ExecutorTask::getFinishTime, now)
                .set(ExecutorTask::getErrorMessage, "Task expired before dispatch")
                //
                .eq(ExecutorTask::getStatus, ExecutorTaskStatus.PENDING)
                .le(ExecutorTask::getDeadlineTime, now)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unknownRunningTasks(LocalDateTime now) {
        return lambdaUpdate()
                .set(ExecutorTask::getStatus, ExecutorTaskStatus.UNKNOWN)
                .set(ExecutorTask::getFinishTime, now)
                .set(ExecutorTask::getErrorMessage, "Task deadline exceeded; send result is uncertain")
                //
                .in(ExecutorTask::getStatus, List.of(ExecutorTaskStatus.DISPATCHED, ExecutorTaskStatus.ACCEPTED, ExecutorTaskStatus.RUNNING))
                .le(ExecutorTask::getDeadlineTime, now)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean markDispatchUnknown(ExecutorTask entity, String message) {
        return lambdaUpdate()
                .set(ExecutorTask::getStatus, ExecutorTaskStatus.UNKNOWN)
                .set(ExecutorTask::getFinishTime, LocalDateTime.now())
                .set(ExecutorTask::getErrorMessage, "Task deadline exceeded; send result is uncertain")
                //
                .eq(ExecutorTask::getId, entity.getId())
                .eq(ExecutorTask::getStatus, ExecutorTaskStatus.DISPATCHED)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTaskEvent(Executor executor, ExecutorTaskResponse event, ExecutorTaskStatus status) {
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case ACCEPTED -> {
                return lambdaUpdate()
                        .set(ExecutorTask::getStatus, ExecutorTaskStatus.ACCEPTED)
                        .set(ExecutorTask::getAcceptedTime, now)
                        .setSql("error_message = NULL")
                        //
                        .eq(ExecutorTask::getId, event.taskId())
                        .eq(ExecutorTask::getExecutorId, executor.getId())
                        .eq(ExecutorTask::getAssignmentId, event.assignmentId())
                        .in(ExecutorTask::getStatus, List.of(ExecutorTaskStatus.PENDING, ExecutorTaskStatus.DISPATCHED))
                        //
                        .update();
            }
            case RUNNING -> {
                return lambdaUpdate()
                        .set(ExecutorTask::getStatus, ExecutorTaskStatus.RUNNING)
                        .set(ExecutorTask::getStartTime, now) // TODO COALESCE
                        //
                        .eq(ExecutorTask::getId, event.taskId())
                        .eq(ExecutorTask::getExecutorId, executor.getId())
                        .eq(ExecutorTask::getAssignmentId, event.assignmentId())
                        .in(ExecutorTask::getStatus, List.of(ExecutorTaskStatus.DISPATCHED, ExecutorTaskStatus.ACCEPTED))
                        //
                        .update();
            }
            case SUCCESS, FAILED, UNKNOWN -> {
                return lambdaUpdate()
                        .set(ExecutorTask::getStatus, status)
                        .set(ExecutorTask::getFinishTime, event.finishedTime() == null ? now : DateTimeUtils.parseMillis(event.finishedTime()))
                        .set(ExecutorTask::getErrorMessage, event.errorMessage())
                        .set(ExecutorTask::getReceiptCode, event.receiptCode())
                        //
                        .eq(ExecutorTask::getId, event.taskId())
                        .eq(ExecutorTask::getExecutorId, executor.getId())
                        .eq(ExecutorTask::getAssignmentId, event.assignmentId())
                        .in(ExecutorTask::getStatus, List.of(ExecutorTaskStatus.DISPATCHED, ExecutorTaskStatus.ACCEPTED, ExecutorTaskStatus.RUNNING, ExecutorTaskStatus.UNKNOWN))
                        //
                        .update();
            }
        }

        return false;
    }
}
