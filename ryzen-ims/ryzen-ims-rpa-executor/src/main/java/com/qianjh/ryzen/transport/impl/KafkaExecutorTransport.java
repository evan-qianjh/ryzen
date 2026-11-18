package com.qianjh.ryzen.transport.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.model.*;
import com.qianjh.ryzen.transport.ExecutorTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * kafka实现的执行器通信层
 *
 * @author QianJH
 */
@Component
@RequiredArgsConstructor
public class KafkaExecutorTransport implements ExecutorTransport {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ExecutorProperties properties;

    private final Gson GSON = new GsonBuilder()
            .create();

    @Override
    public void register(ExecutorSnapshot snapshot) {
        send(properties.getRegisterTopic(), snapshot.executorCode(), snapshot);
    }

    @Override
    public void heartbeat(ExecutorSnapshot snapshot) {
        send(properties.getHeartbeatTopic(), snapshot.executorCode(), snapshot);
    }

    @Override
    public void taskAccepted(ExecutorTaskRequest task) {
        send(properties.getTaskResponseTopic(), IdUtils.toString(task.taskId()), new TaskLifecycleEvent(task.taskId(), "ACCEPTED", null));
    }

    @Override
    public void taskRunning(ExecutorTaskRequest task) {
        send(properties.getTaskResponseTopic(), IdUtils.toString(task.taskId()), new TaskLifecycleEvent(task.taskId(), "RUNNING", null));
    }

    @Override
    public void taskFinished(ExecutorTaskResponse result) {
        send(properties.getTaskResponseTopic(), IdUtils.toString(result.taskId()), result);
    }

    @Override
    public void collectedMessage(ExecutorMessage message) {
        send(properties.getMessageTopic(), message.conversationCode(), message);
    }

    private void send(String topic, String key, Object value) {
        kafkaTemplate.send(topic, key, GSON.toJson(value));
    }

    private record TaskLifecycleEvent(Long taskId, String status, String errorMessage) {
    }
}
