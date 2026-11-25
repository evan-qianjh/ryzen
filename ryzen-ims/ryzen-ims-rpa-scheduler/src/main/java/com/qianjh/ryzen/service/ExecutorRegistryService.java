package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.ims.enums.ExecutorStatus;
import com.qianjh.ryzen.ims.enums.SchedulerReportType;
import com.qianjh.ryzen.ims.model.ExecutorSnapshot;
import com.qianjh.ryzen.ims.model.SchedulerReport;
import com.qianjh.ryzen.transport.KafkaSchedulerTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorRegistryService {
    private final ExecutorService executorService;
    private final KafkaSchedulerTransport transport;
    private final SchedulerTaskService sendTaskService;

    /**
     * 注册
     *
     * @param snapshot 快照
     */
    public void register(ExecutorSnapshot snapshot) {
        validate(snapshot);
        try {
            // 如果不存在则创建执行器
            Executor executor = executorService.getByCode(snapshot.executorCode());
            if (executor == null) {
                log.error("收到不存在执行器的注册请求 ::: {}", snapshot.executorCode());
                return;
            }
            // 注册
            executorService.register(executor, snapshot);
        } catch (DuplicateKeyException e) {
            // TODO 防止同一个平台账号同时被两个执行器接管。
            throw new IllegalStateException("Account is already bound to another executor: " + snapshot.executorCode(), e);
        }

        log.info("执行器注册成功 ::: {}", snapshot.executorCode());

        // 如果就绪，调度任务请求
//        if (snapshot.status() == ExecutorStatus.READY) {
//            sendTaskService.dispatchTaskRequest(snapshot.imType(), snapshot.accountCode());
//        }
    }

    /**
     * 心跳
     *
     * @param snapshot 快照
     */
    public void heartbeat(ExecutorSnapshot snapshot, long timestamp) {
        validate(snapshot);
        Executor executor = executorService.getByCode(snapshot.executorCode());
        if (executor == null) {
            log.warn("收到不存在执行器的心跳 ::: executor={}", snapshot.executorCode());
            return;
        }
        if (executor.getRegisteredTime() == null) {
            log.warn("收到未注册执行器的心跳 ::: executor={}", snapshot.executorCode());
            return;
        }

        executorService.heartbeat(executor, snapshot, timestamp);

        // 如果就绪，调度任务
        if (snapshot.status() == ExecutorStatus.READY) {
            sendTaskService.dispatchTaskRequest(snapshot.assignmentId());
        }
    }

    /**
     * 状态
     *
     * @param snapshot 快照
     */
    public void status(ExecutorSnapshot snapshot) {
        validate(snapshot);
        Executor executor = executorService.getByCode(snapshot.executorCode());
        if (executor == null) {
            log.warn("Heartbeat received from unregistered executor: {}", snapshot.executorCode());
            return;
        }

        // 启动中
        if (ExecutorStatus.STARTING == snapshot.status()) {
            SchedulerReport report = SchedulerReport.builder()
                    .assignmentId(snapshot.assignmentId())
                    .type(SchedulerReportType.EXECUTOR_STARTING)
                    .build();
            transport.schedulerReport(report);
        }
        // 等待登录
        else if (ExecutorStatus.WAIT_LOGIN == snapshot.status()) {
            SchedulerReport report = SchedulerReport.builder()
                    .assignmentId(snapshot.assignmentId())
                    .type(SchedulerReportType.EXECUTOR_WAIT_LOGIN)
                    .build();
            transport.schedulerReport(report);
        }
        // 已启动
        else if (ExecutorStatus.STARTED == snapshot.status()) {
            SchedulerReport report = SchedulerReport.builder()
                    .assignmentId(snapshot.assignmentId())
                    .type(SchedulerReportType.EXECUTOR_STARTED)
                    .build();
            transport.schedulerReport(report);
        }
        // 已停止
        else if (snapshot.status() == ExecutorStatus.STOPPED) {
            SchedulerReport report = SchedulerReport.builder()
                    .assignmentId(snapshot.assignmentId())
                    .type(SchedulerReportType.EXECUTOR_STOPPED)
                    .build();
            transport.schedulerReport(report);
        }
    }

    private void validate(ExecutorSnapshot snapshot) {
        if (snapshot == null || StringUtils.isBlank(snapshot.executorCode()) || snapshot.status() == null) {
            throw new IllegalArgumentException("Invalid executor snapshot");
        }
    }
}
