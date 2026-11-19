package com.qianjh.ryzen.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.model.*;
import com.qianjh.ryzen.service.CollectedMessageService;
import com.qianjh.ryzen.service.ExecutorRegistryService;
import com.qianjh.ryzen.service.SchedulerCommandService;
import com.qianjh.ryzen.service.SendTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSchedulerListener {
    private final Gson gson = new GsonBuilder().create();
    private final SendTaskService sendTaskService;
    private final ExecutorRegistryService executorRegistryService;
    private final CollectedMessageService collectedMessageService;
    private final SchedulerCommandService schedulerCommandService;

    /**
     * 聚合聊天发送消息
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.command-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onCommand(String payload) {
        log.info("收到指令 ::: {}", payload);
        handle("command", payload, SchedulerCommand.class, schedulerCommandService::handle);
    }

    /**
     * 聚合聊天发送消息
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.send-request-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onSendRequest(String payload) {
        handle("send request", payload, SchedulerRequest.class, sendTaskService::accept);
    }

    /**
     * 执行器注册
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.executor-register-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onExecutorRegister(String payload) {
        handle("executor register", payload, ExecutorSnapshot.class, executorRegistryService::register);
    }

    /**
     * 执行器心跳
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.executor-heartbeat-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onExecutorHeartbeat(String payload) {
        handle("executor heartbeat", payload, ExecutorSnapshot.class, executorRegistryService::heartbeat);
    }

    /**
     * 执行器状态
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.executor-status-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onExecutorStatus(String payload) {
        handle("executor status", payload, ExecutorSnapshot.class, executorRegistryService::status);
    }

    /**
     * 执行器反馈的任务结果
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.executor-task-response-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onExecutorTaskResponse(String payload) {
        handle("executor task response", payload, ExecutorTaskResponse.class, sendTaskService::handleTaskResponse);
    }

    /**
     * 执行器采集的消息
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.executor-message-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onCollectedMessage(String payload) {
        handle("collected message", payload, ExecutorMessage.class, collectedMessageService::forward);
    }

    /**
     * 处理
     *
     * @param name     名称
     * @param payload  报文体
     * @param type     类型
     * @param consumer 消费者
     * @param <T>      范型
     */
    private <T> void handle(String name, String payload, Class<T> type, java.util.function.Consumer<T> consumer) {
        try {
            T event = gson.fromJson(payload, type);
            consumer.accept(event);
        } catch (Exception e) {
            log.error("Failed to handle {} payload={}", name, payload, e);
            throw new IllegalStateException("Failed to handle " + name, e);
        }
    }
}
