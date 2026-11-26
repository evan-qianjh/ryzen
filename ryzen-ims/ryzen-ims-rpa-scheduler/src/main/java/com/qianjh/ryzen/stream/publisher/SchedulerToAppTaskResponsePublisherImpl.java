package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.domain.task.type.SchedulerToAppTaskResponse;
import com.qianjh.ryzen.stream.producer.SchedulerToAppStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerToAppTaskResponsePublisherImpl implements SchedulerToAppTaskResponse.Publisher {
    private final SchedulerToAppStreamProducer producer;

    @Override
    public void publish(SchedulerToAppTaskResponse.Body body) {
        StreamPayload<SchedulerToAppTaskResponse.Body> payload = StreamPayload.<SchedulerToAppTaskResponse.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, body.code(), null);
    }
}
