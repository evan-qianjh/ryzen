package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.SchedulerProperties;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.model.ExecutorTaskRequest;
import com.qianjh.ryzen.model.ExecutorTaskResponse;
import com.qianjh.ryzen.model.SchedulerRequest;
import com.qianjh.ryzen.service.ExecutorService;
import com.qianjh.ryzen.service.ExecutorTaskService;
import com.qianjh.ryzen.service.SendTaskService;
import com.qianjh.ryzen.service.TaskDispatchService;
import com.qianjh.ryzen.transport.SchedulerKafkaTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendTaskServiceImpl implements SendTaskService {
    private final SchedulerProperties properties;
    private final ExecutorService executorService;
    private final SchedulerKafkaTransport transport;
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
        ExecutorTask task = executorTaskService.createIfAbsent(request.code(), request.imType(), request.accountCode(), request.conversationCode(), request.content(), deadlineTime);

        // 校验
        if (!task.getImType().equals(request.imType())
                || !task.getAccountCode().equals(request.accountCode())
                || !task.getConversationCode().equals(request.conversationCode())) {
            throw new IllegalArgumentException("id was reused with a different payload: " + request.code());
        }

        // 待执行
        if (task.getStatus() == ExecutorTaskStatus.PENDING) {
            // 调度
            dispatchTaskRequest(request.imType(), request.accountCode());
        }
    }

    /**
     * 分发待执行
     *
     * @param accountCode
     */
    @Override
    public void dispatchTaskRequest(ImType imType, String accountCode) {
        if (!StringUtils.hasText(accountCode)) {
            return;
        }

        // 找出执行器
        Executor executor = executorService.getByAccount(imType, accountCode);

        // 如果没有可用的执行器
        if (executor == null || executor.getStatus() != ExecutorStatus.READY) {
            log.warn("无可用执行器 ::: imType={}, accountCode={}", imType, accountCode);
            return;
        }

        // 预留执行器并领取任务，注意避免并发调度或进程中断造成双派发。
        ExecutorTask task = taskDispatchService.claimNextTask(executor);
        if (task == null) {
            log.warn("无可调度任务 ::: imType={}, accountCode={}", imType, accountCode);
            return;
        }

        // build
        ExecutorTaskRequest payload = ExecutorTaskRequest.builder()
                .imType(executor.getImType())
                .accountCode(accountCode)
                .executorCode(executor.getCode())
                .taskId(task.getId())
                .conversationCode(task.getConversationCode())
                .content(task.getContent())
                .build();
        try {
            // 等待 Kafka broker 确认后再结束本次调度调用；失败时结果可能不确定，不自动重发。
            transport.dispatch(payload).get(10, TimeUnit.SECONDS);
            log.info("Task dispatched: taskId={}, executorId={}, accountCode={}",
                    task.getId(), executor.getId(), accountCode);
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
        if (response == null || response.executorCode() == null || blank(response.accountCode()) || response.taskId() == null) {
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
        Executor executor = executorService.getByCode(response.imType(), response.executorCode());
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
            dispatchTaskRequest(response.imType(), response.accountCode());

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
