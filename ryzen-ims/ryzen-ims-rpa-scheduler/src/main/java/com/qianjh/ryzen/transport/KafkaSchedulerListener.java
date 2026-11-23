package com.qianjh.ryzen.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.model.*;
import com.qianjh.ryzen.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSchedulerListener {
    private final Gson gson = new GsonBuilder().create();
    private final SchedulerTaskService sendTaskService;
    private final ExecutorRegistryService executorRegistryService;
    private final ExecutorCommandService executorCommandService;
    private final CollectedMessageService collectedMessageService;
    private final SchedulerCommandService schedulerCommandService;

    /**
     * 命令
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.command-request-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void onCommand(String payload) {
        log.info("收到指令 ::: {}", payload);
        handle("command", payload, SchedulerCommandRequest.class, schedulerCommandService::handle);
    }

    /**
     * 聚合聊天发送消息
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.scheduler.task-request-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "defaultKafkaListenerContainerFactory"
    )
    public void onSendRequest(String payload) {
        handle("send request", payload, SchedulerTaskRequest.class, sendTaskService::accept);
    }

    /**
     * 执行器命令响应
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.executor.command-response-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onExecutorCommandResponse(String payload) {
        handle("executor command response", payload, ExecutorCommandResponse.class, executorCommandService::handle);
    }

    /**
     * 执行器注册
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.executor.register-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onExecutorRegister(String payload) {
        handle("executor register", payload, ExecutorSnapshot.class, executorRegistryService::register);
    }

    /**
     * 执行器心跳
     *
     * @param record 报文
     */
    @KafkaListener(
            topics = "${ryzen.executor.heartbeat-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "publicKafkaListenerContainerFactory"
    )
    public void onExecutorHeartbeat(ConsumerRecord<String, String> record) {
        // 搭配 topic的message.timestamp.type=LogAppendTime，获取的是消息发送到broker的时间，避免executor与scheduler时间不一致
        long timestamp = record.timestamp();
        String payload = record.value();
        // 丢弃1分钟前的消息
        long expireTime = System.currentTimeMillis() - 60 * 1000L;
        if (timestamp < expireTime) {
            log.error("忽略过期消息 ::: timestamp={}, payload={}", timestamp, payload);
            return;
        }

        ExecutorSnapshot snapshot = gson.fromJson(payload, ExecutorSnapshot.class);
        executorRegistryService.heartbeat(snapshot, timestamp);

//        handle("executor heartbeat", payload, ExecutorSnapshot.class, executorRegistryService::heartbeat);
    }

    /**
     * 执行器状态
     *
     * @param payload 报文
     */
    @KafkaListener(
            topics = "${ryzen.executor.status-topic}",
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
            topics = "${ryzen.executor.task-response-topic}",
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
            topics = "${ryzen.executor.message-topic}",
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
