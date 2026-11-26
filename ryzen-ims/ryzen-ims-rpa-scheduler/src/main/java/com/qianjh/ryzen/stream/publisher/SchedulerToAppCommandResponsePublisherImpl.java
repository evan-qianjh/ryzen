package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.domain.command.type.SchedulerToAppCommandResponse;
import com.qianjh.ryzen.stream.producer.SchedulerToAppStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerToAppCommandResponsePublisherImpl implements SchedulerToAppCommandResponse.Publisher {
    private final SchedulerToAppStreamProducer producer;

    @Override
    public void publish(SchedulerToAppCommandResponse.Body body) {
        StreamPayload<SchedulerToAppCommandResponse.Body> payload = StreamPayload.<SchedulerToAppCommandResponse.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, body.code(), null);
    }
}
