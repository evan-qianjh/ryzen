package com.qianjh.ryzen.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.config.SchedulerProperties;
import com.qianjh.ryzen.model.ExecutorTaskRequest;
import com.qianjh.ryzen.model.SchedulerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaSchedulerTransport {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SchedulerProperties properties;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * 分发任务给执行器
     *
     * @param task 任务
     * @return 结果
     */
    public CompletableFuture<SendResult<String, String>> dispatch(ExecutorTaskRequest task) {
        return send(properties.getExecutorTaskRequestTopic(), task.accountCode(), task);
    }

    /**
     * 发布任务给接入方
     *
     * @param message 消息
     * @return 结果
     */
    public CompletableFuture<SendResult<String, String>> publishCollectedMessage(SchedulerMessage message) {
        return send(properties.getMessageTopic(), message.messageCode(), message);
    }

    /**
     * 发送
     *
     * @param topic   topic
     * @param key     key
     * @param payload payload
     * @return 结果
     */
    private CompletableFuture<SendResult<String, String>> send(String topic, String key, Object payload) {
        return kafkaTemplate.send(topic, key, gson.toJson(payload));
    }
}
