package com.qianjh.ryzen.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.config.SchedulerProperties;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.model.ExecutorCommandRequest;
import com.qianjh.ryzen.model.ExecutorTaskRequest;
import com.qianjh.ryzen.model.SchedulerCommandResponse;
import com.qianjh.ryzen.model.SchedulerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaSchedulerTransport {
    private final KafkaTemplate<String, String> defaultKafkaTemplate;
    private final KafkaTemplate<String, String> publicKafkaTemplate;
    private final SchedulerProperties schedulerProperties;
    private final ExecutorProperties executorProperties;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * 向执行器发送指令请求
     *
     * @param request 请求
     * @return 结果
     */
    public CompletableFuture<SendResult<String, String>> executorCommandRequest(ExecutorCommandRequest request) {
        return publicKafkaTemplate.send(
                executorProperties.getCommandRequestTopic(),
                request.executorCode(),
                gson.toJson(request)
        );
    }

    /**
     * 调度器回复指令响应
     *
     * @param response 响应
     * @return 结果
     */
    public CompletableFuture<SendResult<String, String>> schedulerCommandResponse(SchedulerCommandResponse response) {
        return publicKafkaTemplate.send(
                executorProperties.getCommandResponseTopic(),
                response.code(), // TODO
                gson.toJson(response)
        );
    }

    /**
     * 分发任务给执行器
     *
     * @param task 任务
     * @return 结果
     */
    public CompletableFuture<SendResult<String, String>> executorTask(ExecutorTaskRequest task) {
        return publicKafkaTemplate.send(
                executorProperties.getTaskRequestTopic(),
                IdUtils.toString(task.assignmentId()),
                gson.toJson(task)
        );
    }

    /**
     * 发布任务给接入方
     *
     * @param message 消息
     * @return 结果
     */
    public CompletableFuture<SendResult<String, String>> schedulerMessage(SchedulerMessage message) {
        return defaultKafkaTemplate.send(
                schedulerProperties.getMessageTopic(),
                message.messageCode(),
                gson.toJson(message)
        );
    }
}
