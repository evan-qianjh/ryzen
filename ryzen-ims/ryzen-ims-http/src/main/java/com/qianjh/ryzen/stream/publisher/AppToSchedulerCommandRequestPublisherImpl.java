package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.domain.command.type.AppToSchedulerCommandRequest;
import com.qianjh.ryzen.stream.producer.AppToSchedulerStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppToSchedulerCommandRequestPublisherImpl implements AppToSchedulerCommandRequest.Publisher {
    private final AppToSchedulerStreamProducer producer;

    @Override
    public void publish(AppToSchedulerCommandRequest.Body body) {
        StreamPayload<AppToSchedulerCommandRequest.Body> payload = StreamPayload.<AppToSchedulerCommandRequest.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, body.code(), null);
    }
}
