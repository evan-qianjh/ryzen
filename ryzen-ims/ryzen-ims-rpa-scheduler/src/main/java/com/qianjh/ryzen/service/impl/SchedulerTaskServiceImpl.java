package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.SchedulerProperties;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.ims.enums.ExecutorStatus;
import com.qianjh.ryzen.ims.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerTaskResult;
import com.qianjh.ryzen.ims.enums.SchedulerToAppTaskResponseResult;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.task.AppToSchedulerTaskRequest;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerTaskResponse;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.task.SchedulerToAppTaskResponse;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.command.SchedulerToExecutorTaskRequest;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.ExecutorTaskService;
import com.qianjh.ryzen.service.SchedulerTaskService;
import com.qianjh.ryzen.service.TaskDispatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerTaskServiceImpl implements SchedulerTaskService {
    private final SchedulerProperties properties;
    private final ExecutorService executorService;
    private final ExecutorTaskService executorTaskService;
    private final TaskDispatchService taskDispatchService;
    private final SchedulerToAppTaskResponse.Publisher schedulerToAppTaskResponsePublisher;
    private final SchedulerToExecutorTaskRequest.Publisher schedulerToExecutorTaskRequestPublisher;

    /**
     * 接受任务
     *
     * @param request 请求
     */
    @Override
    public void accept(AppToSchedulerTaskRequest.Body request) {
        validate(request);
        LocalDateTime now = LocalDateTime.now();

        // 设置截止时间
        LocalDateTime deadlineTime = request.deadlineTime() == null || request.deadlineTime() <= 0
                ? now.plusSeconds(properties.getTaskTimeoutSeconds())
                : DateTimeUtils.parseMillis(request.deadlineTime());
        if (deadlineTime.isBefore(now)) {
            throw new IllegalArgumentException("deadlineTime must be in the future");
        }

        // 创建执行器任务
        SchedulerToAppTaskResponseResult result;
        ExecutorTask task = executorTaskService.getByUk(request.code());
        if (task == null) {
            result = SchedulerToAppTaskResponseResult.ACCEPTED;
            task = executorTaskService.create(request.code(), request.assignmentId(), request.conversationCode(),
                    request.content(), deadlineTime);
        } else {
            result = SchedulerToAppTaskResponseResult.DUPLICATE;
        }

        // 回执-受理
        schedulerToAppTaskResponsePublisher.publish(
                SchedulerToAppTaskResponse.Body.builder()
                        .code(request.code())
                        .result(result)
                        .build()
        );

        // 待执行
        if (task.getStatus() == ExecutorTaskStatus.PENDING) {
            // 调度
            dispatchTaskRequest(request.assignmentId());
        }
    }

    /**
     * 分发待执行
     *
     * @param assignmentId 分配
     */
    @Override
    public void dispatchTaskRequest(Long assignmentId) {
        if (assignmentId == null) {
            log.error("dispatchTaskRequest assignmentId is null");
            return;
        }

        // 找出执行器
        Executor executor = executorService.getByAssignment(assignmentId);
        if (executor == null) {
            log.warn("不存在的执行器 ::: assignmentId={}", assignmentId);
            return;
        }
        // 如果没有可用的执行器
        if (executor.getStatus() != ExecutorStatus.READY) {
            log.warn("执行器状态不可用 ::: assignmentId={}", assignmentId);
            return;
        }

        // 预留执行器并领取任务，注意避免并发调度或进程中断造成双派发。
        ExecutorTask task = taskDispatchService.claimNextTask(executor);
        if (task == null) {
            log.debug("无可调度任务 ::: assignmentId={}", assignmentId);
            return;
        }

        // build
        try {
            schedulerToExecutorTaskRequestPublisher.publish(
                    SchedulerToExecutorTaskRequest.Body.builder()
                            .assignmentId(assignmentId)
                            .executorCode(executor.getCode())
                            .taskId(task.getId())
                            .conversationCode(task.getConversationCode())
                            .content(task.getContent())
                            .build()
            );
            log.info("Task dispatched: taskId={}, executorId={}, assignmentId={}",
                    task.getId(), executor.getId(), assignmentId);
        } catch (Exception e) {
            executorTaskService.markDispatchUnknown(task, e.getMessage());
            log.error("Dispatch outcome is uncertain; task will not be retried automatically: taskId={}",
                    task.getId(), e);
        }
    }

    /**
     * 处理任务执行结果
     *
     * @param body 结果
     */
    @Override
    public void handleTaskResponse(ExecutorToSchedulerTaskResponse.Body body) {
        if (body == null || body.executorCode() == null || body.assignmentId() == null || body.taskId() == null) {
            log.warn("Ignore invalid executor task event: {}", body);
            return;
        }

        ExecutorToSchedulerTaskResult result = body.result();

        // 执行器
        Executor executor = executorService.getByCode(body.executorCode());
        if (executor == null) {
            log.warn("Task event references unknown executor={}", body.executorCode());
            return;
        }

        // 任务
        ExecutorTask task = executorTaskService.getById(body.taskId());
        if (task == null) {
            log.warn("Task event references unknown taskId={}", body.taskId());
            return;
        }

        // 更新任务结果
        boolean success = executorTaskService.updateTaskEvent(executor, body, result);
        if (!success) {
            log.debug("Task event ignored due to duplicate or invalid transition: taskId={}, status={}",
                    body.taskId(), result);
            return;
        }

        // 反馈给APP
        SchedulerToAppTaskResponseResult toAppResult = convertToAppResult(result);
        if (toAppResult != null) {
            schedulerToAppTaskResponsePublisher.publish(
                    SchedulerToAppTaskResponse.Body.builder()
                            .code(task.getCode())
                            .result(toAppResult)
                            .build()
            );
        }

        // 已关闭
        if (result.isClosed()) {
            //可以继续下一个
            executorService.markExecutorReadyAfterTask(executor.getId(), body.taskId());
            dispatchTaskRequest(body.assignmentId());
        }
    }

    /**
     * 转换成给APP的状态
     *
     * @param status 任务状态
     * @return 给APP的结果
     */
    private SchedulerToAppTaskResponseResult convertToAppResult(ExecutorToSchedulerTaskResult status) {
        switch (status) {
            case SUCCESS -> {
                return SchedulerToAppTaskResponseResult.SUCCESS;
            }
            case FAILED -> {
                return SchedulerToAppTaskResponseResult.FAILED;
            }
            case UNKNOWN -> {
                return SchedulerToAppTaskResponseResult.UNKNOWN;
            }
            case CANCELED -> {
                // TODO
            }
        }
        return null;
    }

    /**
     * 校验
     *
     * @param request 请求
     */
    private void validate(AppToSchedulerTaskRequest.Body request) {
        if (request == null || blank(request.code()) || request.assignmentId() == null
                || StringUtils.isBlank(request.conversationCode()) || StringUtils.isBlank(request.content())) {
            throw new IllegalArgumentException("Invalid send message command");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
