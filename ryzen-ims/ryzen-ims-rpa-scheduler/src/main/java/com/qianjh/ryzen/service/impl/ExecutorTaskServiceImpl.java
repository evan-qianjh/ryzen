package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.ims.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerTaskResult;
import com.qianjh.ryzen.ims.enums.SchedulerToAppTaskResponseResult;
import com.qianjh.ryzen.ims.model.ExecutorTaskResponse;
import com.qianjh.ryzen.mapper.ExecutorTaskMapper;
import com.qianjh.ryzen.service.ExecutorTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExecutorTaskServiceImpl extends ServiceImpl<ExecutorTaskMapper, ExecutorTask> implements ExecutorTaskService {

    @Override
    public ExecutorTask getByUk(String code) {
        return getOne(new LambdaQueryWrapper<ExecutorTask>()
                .eq(ExecutorTask::getCode, code)
        );
    }

    @Override
    public ExecutorTask getByCode(String code) {
        return getOne(new LambdaQueryWrapper<ExecutorTask>()
                .eq(ExecutorTask::getCode, code)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ExecutorTask create(String code, Long assignmentId, String conversationCode, String content, LocalDateTime deadlineTime) {
        ExecutorTask entity = ExecutorTask.builder()
                .code(code)
                .assignmentId(assignmentId)
                .conversationCode(conversationCode)
                .content(content)
                .status(ExecutorTaskStatus.PENDING)
                .deadlineTime(deadlineTime)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ExecutorTask createIfAbsent(String code, Long assignmentId, String conversationCode, String content, LocalDateTime deadlineTime) {
        ExecutorTask entity = getByUk(code);
        if (entity != null) {
            return entity;
        }
        return create(code, assignmentId, conversationCode, content, deadlineTime);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean timeoutPendingTasks(LocalDateTime now) {
        // TODO 通知app TIMEOUT
        SchedulerToAppTaskResponseResult toAppResult = SchedulerToAppTaskResponseResult.TIMEOUT;

        return lambdaUpdate()
                .set(ExecutorTask::getStatus, ExecutorTaskStatus.TIMEOUT)
                .set(ExecutorTask::getFinishedTime, now)
                .set(ExecutorTask::getErrorMessage, "Task expired before dispatch")
                //
                .eq(ExecutorTask::getStatus, ExecutorTaskStatus.PENDING)
                .le(ExecutorTask::getDeadlineTime, now)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean unknownRunningTasks(LocalDateTime now) {
        // TODO 通知app UNKNOWN
        SchedulerToAppTaskResponseResult toAppResult = SchedulerToAppTaskResponseResult.UNKNOWN;

        return lambdaUpdate()
                .set(ExecutorTask::getStatus, ExecutorTaskStatus.UNKNOWN)
                .set(ExecutorTask::getFinishedTime, now)
                .set(ExecutorTask::getErrorMessage, "Task deadline exceeded; send result is uncertain")
                //
                .in(ExecutorTask::getStatus, List.of(ExecutorTaskStatus.DISPATCHED, ExecutorTaskStatus.ACCEPTED, ExecutorTaskStatus.RUNNING))
                .le(ExecutorTask::getDeadlineTime, now)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean markDispatchUnknown(ExecutorTask entity, String message) {
        // TODO 通知app UNKNOWN
        return lambdaUpdate()
                .set(ExecutorTask::getStatus, ExecutorTaskStatus.UNKNOWN)
                .set(ExecutorTask::getFinishedTime, LocalDateTime.now())
                .set(ExecutorTask::getErrorMessage, message)
                //
                .eq(ExecutorTask::getId, entity.getId())
                .eq(ExecutorTask::getStatus, ExecutorTaskStatus.DISPATCHED)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateTaskEvent(Executor executor, ExecutorTaskResponse event, ExecutorToSchedulerTaskResult status) {
        // TODO 应该取写入kafka时间
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
                        .set(ExecutorTask::getStartedTime, now)
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
                        .set(ExecutorTask::getFinishedTime, now)
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
