package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.ims.enums.ExecutorStatus;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerReportEventType;
import com.qianjh.ryzen.ims.enums.SchedulerReportEventType;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportEvent;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportHeartbeat;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportRegister;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.SchedulerToAppReportEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorRegistryService {
    private final ExecutorService executorService;
    private final SchedulerTaskService sendTaskService;
    private final SchedulerToAppReportEvent.Publisher schedulerToAppReportEventPublisher;

    /**
     * 注册
     *
     * @param body 快照
     */
    public void register(ExecutorToSchedulerReportRegister.Body body) {
        try {
            // 如果不存在则创建执行器
            Executor executor = executorService.getByCode(body.executorCode());
            if (executor == null) {
                log.error("收到不存在执行器的注册请求 ::: {}", body.executorCode());
                return;
            }
            // 注册
            executorService.register(executor, body);
        } catch (DuplicateKeyException e) {
            // TODO 防止同一个平台账号同时被两个执行器接管。
            throw new IllegalStateException("Account is already bound to another executor: " + body.executorCode(), e);
        }

        log.info("执行器注册成功 ::: {}", body.executorCode());

        // 如果就绪，调度任务请求
//        if (snapshot.status() == ExecutorStatus.READY) {
//            sendTaskService.dispatchTaskRequest(snapshot.imType(), snapshot.accountCode());
//        }
    }

    /**
     * 心跳
     *
     * @param body 快照
     */
    public void heartbeat(ExecutorToSchedulerReportHeartbeat.Body body, long timestamp) {
        Executor executor = executorService.getByCode(body.executorCode());
        if (executor == null) {
            log.warn("收到不存在执行器的心跳 ::: executor={}", body.executorCode());
            return;
        }
        if (executor.getRegisteredTime() == null) {
            log.warn("收到未注册执行器的心跳 ::: executor={}", body.executorCode());
            return;
        }

        executorService.heartbeat(executor, body, timestamp);

        // 如果就绪，调度任务
        if (body.status() == ExecutorStatus.READY) {
            sendTaskService.dispatchTaskRequest(body.assignmentId());
        }
    }

    /**
     * 状态
     *
     * @param body 快照
     */
    public void status(ExecutorToSchedulerReportEvent.Body body) {
        Executor executor = executorService.getByCode(body.executorCode());
        if (executor == null) {
            log.warn("Heartbeat received from unregistered executor: {}", body.executorCode());
            return;
        }

        // 启动中
        if (ExecutorToSchedulerReportEventType.STARTING == body.type()) {
            schedulerToAppReportEventPublisher.publish(
                    SchedulerToAppReportEvent.Body.builder()
                            .assignmentId(body.assignmentId())
                            .type(SchedulerReportEventType.EXECUTOR_STARTING)
                            .build()
            );
        }
        // 等待登录
        else if (ExecutorToSchedulerReportEventType.WAIT_LOGIN == body.type()) {
            schedulerToAppReportEventPublisher.publish(
                    SchedulerToAppReportEvent.Body.builder()
                            .assignmentId(body.assignmentId())
                            .type(SchedulerReportEventType.EXECUTOR_WAIT_LOGIN)
                            .build()
            );
        }
        // 已启动
        else if (ExecutorToSchedulerReportEventType.STARTED == body.type()) {
            schedulerToAppReportEventPublisher.publish(
                    SchedulerToAppReportEvent.Body.builder()
                            .assignmentId(body.assignmentId())
                            .type(SchedulerReportEventType.EXECUTOR_STARTED)
                            .build()
            );
        }
        // 已停止
        else if (ExecutorToSchedulerReportEventType.STOPPED == body.type()) {
            schedulerToAppReportEventPublisher.publish(
                    SchedulerToAppReportEvent.Body.builder()
                            .assignmentId(body.assignmentId())
                            .type(SchedulerReportEventType.EXECUTOR_STOPPED)
                            .build()
            );
        }
    }

}
