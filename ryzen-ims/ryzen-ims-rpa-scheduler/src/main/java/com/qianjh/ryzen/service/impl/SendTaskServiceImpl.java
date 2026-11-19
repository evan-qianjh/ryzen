package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.SchedulerProperties;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.model.ExecutorTaskRequest;
import com.qianjh.ryzen.model.ExecutorTaskResponse;
import com.qianjh.ryzen.model.SchedulerRequest;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.ExecutorTaskService;
import com.qianjh.ryzen.service.SendTaskService;
import com.qianjh.ryzen.service.TaskDispatchService;
import com.qianjh.ryzen.transport.KafkaSchedulerTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendTaskServiceImpl implements SendTaskService {
    private final SchedulerProperties properties;
    private final ExecutorService executorService;
    private final KafkaSchedulerTransport transport;
    private final ExecutorTaskService executorTaskService;
    private final TaskDispatchService taskDispatchService;

    /**
     * 接受任务
     *
     * @param request
     */
    @Override
    public void accept(SchedulerRequest request) {
        validate(request);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineTime = request.deadlineTime() == null || request.deadlineTime() <= 0
                ? now.plusSeconds(properties.getTaskTimeoutSeconds()) : DateTimeUtils.parseMillis(request.deadlineTime());
        if (deadlineTime.isBefore(now)) {
            throw new IllegalArgumentException("deadlineTime must be in the future");
        }

        // 创建执行器任务
        ExecutorTask task = executorTaskService.createIfAbsent(request.code(), request.assignmentId(), request.conversationCode(), request.content(), deadlineTime);

        // 校验
        if (!task.getAssignmentId().equals(request.assignmentId())
                || !task.getConversationCode().equals(request.conversationCode())) {
            throw new IllegalArgumentException("id was reused with a different payload: " + request.code());
        }

        // 待执行
        if (task.getStatus() == ExecutorTaskStatus.PENDING) {
            // 调度
            dispatchTaskRequest(request.assignmentId());
        }
    }

    /**
     * 分发待执行
     *
     * @param assignmentId
     */
    @Override
    public void dispatchTaskRequest(Long assignmentId) {
        if (assignmentId == null) {
            log.error("dispatchTaskRequest assignmentId is null");
            return;
        }

        // 找出执行器
        Executor executor = executorService.getByAssignment(assignmentId);

        // 如果没有可用的执行器
        if (executor == null || executor.getStatus() != ExecutorStatus.READY) {
            log.warn("无可用执行器 ::: assignmentId={}", assignmentId);
            return;
        }

        // 预留执行器并领取任务，注意避免并发调度或进程中断造成双派发。
        ExecutorTask task = taskDispatchService.claimNextTask(executor);
        if (task == null) {
            log.warn("无可调度任务 ::: assignmentId={}", assignmentId);
            return;
        }

        // build
        ExecutorTaskRequest payload = ExecutorTaskRequest.builder()
                .assignmentId(assignmentId)
//                .imType(executor.getImType())
//                .accountCode(accountCode)
                .executorCode(executor.getCode())
                .taskId(task.getId())
                .conversationCode(task.getConversationCode())
                .content(task.getContent())
                .build();
        try {
            // 等待 Kafka broker 确认后再结束本次调度调用；失败时结果可能不确定，不自动重发。
            transport.executorTask(payload).get(10, TimeUnit.SECONDS);
            log.info("Task dispatched: taskId={}, executorId={}, assignmentId={}",
                    task.getId(), executor.getId(), assignmentId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorTaskService.markDispatchUnknown(task, e.getMessage());
            log.error("Dispatch interrupted; outcome is uncertain: taskId={}", task.getId(), e);
        } catch (Exception e) {
            executorTaskService.markDispatchUnknown(task, e.getMessage());
            log.error("Dispatch outcome is uncertain; task will not be retried automatically: taskId={}",
                    task.getId(), e);
        }
    }

    /**
     * 处理任务执行结果
     *
     * @param response 结果
     */
    @Override
    public void handleTaskResponse(ExecutorTaskResponse response) {
        if (response == null || response.executorCode() == null || response.assignmentId() == null || response.taskId() == null) {
            log.warn("Ignore invalid executor task event: {}", response);
            return;
        }

        ExecutorTaskStatus status;
        try {
            status = response.status();
        } catch (Exception e) {
            log.warn("Ignore unsupported task status: {}", response.status());
            return;
        }

        // 执行器
        Executor executor = executorService.getByCode(response.executorCode());
        if (executor == null) {
            log.warn("Task event references unknown executor={}", response.executorCode());
            return;
        }

        // 任务
        ExecutorTask task = executorTaskService.getById(response.taskId());
        if (task == null) {
            log.warn("Task event references unknown taskId={}", response.taskId());
            return;
        }

        // 更新任务结果
        boolean success = executorTaskService.updateTaskEvent(executor, response, status);
        if (!success) {
            log.debug("Task event ignored due to duplicate or invalid transition: taskId={}, status={}",
                    response.taskId(), status);
            return;
        }

        // 标记执行器可以处理新的任务
        if (status == ExecutorTaskStatus.SUCCESS
                || status == ExecutorTaskStatus.FAILED
                || status == ExecutorTaskStatus.UNKNOWN) {
            executorService.markExecutorReadyAfterTask(executor.getId(), response.taskId());
            dispatchTaskRequest(response.assignmentId());

            // TODO 给聚合服务执行结果
        }
    }

    /**
     * 校验
     *
     * @param request 请求
     */
    private void validate(SchedulerRequest request) {
        if (request == null || blank(request.code()) || blank(request.accountCode())
                || blank(request.conversationCode()) || request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("Invalid send message command");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
