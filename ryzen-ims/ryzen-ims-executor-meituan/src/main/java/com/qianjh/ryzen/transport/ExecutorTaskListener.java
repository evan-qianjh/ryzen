package com.qianjh.ryzen.transport;

import com.google.gson.Gson;
import com.qianjh.ryzen.core.ExecutorWorker;
import com.qianjh.ryzen.model.ExecutorTaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 任务监听器
 *
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorTaskListener {
    private final ExecutorWorker worker;
    private final Gson gson = new Gson();


    @KafkaListener(topics = "${ryzen.executor.task-request-topic}")
    public void onTask(String payload) {
        try {
            ExecutorTaskRequest task = gson.fromJson(payload, ExecutorTaskRequest.class);
            boolean accept = worker.accept(task);
            if (!accept) {
                log.error("接受任务失败 ::: {}", payload);
            }
        } catch (Exception e) {
            log.error("Invalid executor task payload: {}", payload, e);
        }
    }
}
