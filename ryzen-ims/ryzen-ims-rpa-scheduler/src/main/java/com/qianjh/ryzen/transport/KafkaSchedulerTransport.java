package com.qianjh.ryzen.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qianjh.ryzen.config.ExecutorProperties;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.dto.ExecutorCommandRequest;
import com.qianjh.ryzen.ims.dto.ExecutorTaskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaSchedulerTransport {
    private final KafkaTemplate<String, String> publicKafkaTemplate;
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
     * 分发任务给执行器
     *
     * @param task 任务
     * @return 结果
     */
    public CompletableFuture<SendResult<String, String>> executorTaskRequest(ExecutorTaskRequest task) {
        return publicKafkaTemplate.send(
                executorProperties.getTaskRequestTopic(),
                IdUtils.toString(task.assignmentId()),
                gson.toJson(task)
        );
    }
}
