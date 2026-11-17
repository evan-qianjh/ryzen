package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.enums.ExecutorOperationType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.mapper.ExecutorTaskMapper;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.ExecutorTaskService;
import com.qianjh.ryzen.service.TaskDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskDispatchServiceImpl implements TaskDispatchService {

    private final ExecutorService executorService;
    private final ExecutorTaskMapper executorTaskMapper;
    private final ExecutorTaskService executorTaskService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ExecutorTask claimNextTask(Executor executor) {
        // 锁任务
        ExecutorTask task = executorTaskMapper.lock(executor.getAccountCode(), ExecutorTaskStatus.PENDING);
        if (task == null) {
            return null;
        }

        // 执行器分配任务
        boolean success = executorService.lambdaUpdate()
                .set(Executor::getStatus, ExecutorStatus.BUSY)
                .set(Executor::getCurrentOperation, ExecutorOperationType.SEND)
                .set(Executor::getCurrentTaskId, task.getId())
                //
                .eq(Executor::getId, executor.getId())
                .eq(Executor::getStatus, ExecutorStatus.READY)
                .update();
        Assert.isTrue(success, "执行器分配任务失败");

        // 任务分配执行器
        boolean success2 = executorTaskService.lambdaUpdate()
                .set(ExecutorTask::getExecutorId, executor.getId())
                .set(ExecutorTask::getStatus, ExecutorTaskStatus.DISPATCHED)
                .set(ExecutorTask::getDispatchTime, LocalDateTime.now())
                .setSql("error_message = NULL")
                .eq(ExecutorTask::getId, task.getId())
                .eq(ExecutorTask::getStatus, ExecutorTaskStatus.PENDING)
                .update();
        Assert.isTrue(success2, "任务分配执行器失败");

        return task;
    }
}
