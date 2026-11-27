package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.im_message.im_message.AppToSchedulerTaskCreated;
import com.qianjh.ryzen.stream.producer.ImMessageStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppToSchedulerTaskCreatedPublisherImpl implements AppToSchedulerTaskCreated.Publisher {
    private final ImMessageStreamProducer producer;

    @Override
    public void publish(AppToSchedulerTaskCreated.Body body) {
        StreamPayload<AppToSchedulerTaskCreated.Body> payload = StreamPayload.<AppToSchedulerTaskCreated.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), null);
    }
}
