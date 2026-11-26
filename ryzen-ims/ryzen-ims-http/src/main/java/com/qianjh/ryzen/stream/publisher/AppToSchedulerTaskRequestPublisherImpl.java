package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.task.type.AppToSchedulerTaskRequest;
import com.qianjh.ryzen.stream.producer.AppToSchedulerStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppToSchedulerTaskRequestPublisherImpl implements AppToSchedulerTaskRequest.Publisher {
    private final AppToSchedulerStreamProducer producer;

    @Override
    public void publish(AppToSchedulerTaskRequest.Body body) {
        StreamPayload<AppToSchedulerTaskRequest.Body> payload = StreamPayload.<AppToSchedulerTaskRequest.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, body.code(), null);
    }
}
