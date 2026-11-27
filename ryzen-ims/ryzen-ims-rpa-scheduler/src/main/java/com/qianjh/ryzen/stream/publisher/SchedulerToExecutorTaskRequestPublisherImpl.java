package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.command.SchedulerToExecutorTaskRequest;
import com.qianjh.ryzen.stream.producer.SchedulerToExecutorStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerToExecutorTaskRequestPublisherImpl implements SchedulerToExecutorTaskRequest.Publisher {
    private final SchedulerToExecutorStreamProducer producer;

    @Override
    public void publish(SchedulerToExecutorTaskRequest.Body body) {
        StreamPayload<SchedulerToExecutorTaskRequest.Body> payload = StreamPayload.<SchedulerToExecutorTaskRequest.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, body.executorCode(), null);
    }
}
