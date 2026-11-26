package com.qianjh.ryzen.transport.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerTaskResult;
import com.qianjh.ryzen.ims.dto.*;
import com.qianjh.ryzen.transport.ExecutorTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * kafka实现的执行器通信层
 *
 * @author QianJH
 */
@Slf4j
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
    public void command(ExecutorCommandResponse response) {
        send(properties.getCommandResponseTopic(), response.executorCode(), response);
    }

    @Override
    public void status(ExecutorSnapshot snapshot) {
        log.info("上报状态 ::: status={}, snapshot={}", snapshot.status(), snapshot);
        send(properties.getStatusTopic(), snapshot.executorCode(), snapshot);
    }

    @Override
    public void taskAccepted(ExecutorTaskRequest task) {
        ExecutorTaskResponse response = ExecutorTaskResponse.builder()
                .assignmentId(task.assignmentId())
                .executorCode(task.executorCode())
                .taskId(task.taskId())
                .result(ExecutorToSchedulerTaskResult.ACCEPTED)
                .build();
        send(properties.getTaskResponseTopic(), IdUtils.toString(task.taskId()), response);
    }

    @Override
    public void taskRunning(ExecutorTaskRequest task) {
        ExecutorTaskResponse response = ExecutorTaskResponse.builder()
                .assignmentId(task.assignmentId())
                .executorCode(task.executorCode())
                .taskId(task.taskId())
                .result(ExecutorToSchedulerTaskResult.RUNNING)
                .build();
        send(properties.getTaskResponseTopic(), IdUtils.toString(task.taskId()), response);
    }

    @Override
    public void taskFinished(ExecutorTaskResponse result) {
        send(properties.getTaskResponseTopic(), IdUtils.toString(result.taskId()), result);
    }

    @Override
    public void message(ExecutorMessage message) {
        send(properties.getMessageTopic(), message.conversationCode(), message);
    }

    private void send(String topic, String key, Object value) {
        kafkaTemplate.send(topic, key, GSON.toJson(value));
    }

}
