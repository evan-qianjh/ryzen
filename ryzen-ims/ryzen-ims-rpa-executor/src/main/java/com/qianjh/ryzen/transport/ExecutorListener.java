package com.qianjh.ryzen.transport;

import com.google.gson.Gson;
import com.qianjh.ryzen.core.ExecutorWorker;
import com.qianjh.ryzen.framework.stream.model.StreamHeader;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.framework.stream.util.StreamUtils;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.SchedulerToExecutorCommand;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.SchedulerToExecutorTask;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.command.SchedulerToExecutorCommandRequest;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.command.SchedulerToExecutorTaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * 任务监听器
 *
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorListener {
    private final ExecutorWorker worker;
    private final Gson gson = new Gson();

    public static String getStringHeader(Object object) {
        if (object == null) {
            return null;
        }
        return String.valueOf(object);
    }

    @KafkaListener(topics = "${ryzen.executor.scheduler-to-executor-topic}")
    public void onSchedulerToExecutor(Message<String> message) {
        MessageHeaders headers = message.getHeaders();
        String payload = message.getPayload();

        //
        String domain = getStringHeader(headers.get(StreamHeader.DOMAIN));
        String type = getStringHeader(headers.get(StreamHeader.TYPE));

        //
        if (StringUtils.isBlank(domain) || StringUtils.isBlank(type)) {
            log.error("受到异常消息 ::: {}", message);
            return;
        }

        switch (domain) {
            // command
            case SchedulerToExecutorCommand.DOMAIN -> {
                // request
                if (type.equals(SchedulerToExecutorCommandRequest.TYPE)) {
                    StreamPayload<SchedulerToExecutorCommandRequest.Body> streamPayload = StreamUtils.fromJson(payload, SchedulerToExecutorCommandRequest.Body.class);
                    SchedulerToExecutorCommandRequest.Body body = streamPayload.getBody();
                    worker.acceptCommand(body);
                }
            }
            // task
            case SchedulerToExecutorTask.DOMAIN -> {
                // request
                if (type.equals(SchedulerToExecutorTaskRequest.TYPE)) {
                    StreamPayload<SchedulerToExecutorTaskRequest.Body> streamPayload = StreamUtils.fromJson(payload, SchedulerToExecutorTaskRequest.Body.class);
                    SchedulerToExecutorTaskRequest.Body body = streamPayload.getBody();
                    boolean accept = worker.accept(body);
                    if (!accept) {
                        log.error("接受任务失败 ::: {}", payload);
                    }
                }
            }
        }
    }

}
