package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.domain.im_message.type.ImMessageCreatedEvent;
import com.qianjh.ryzen.stream.producer.ImMessageTopic_StreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImMessageCreatedEvent_PublisherImpl implements ImMessageCreatedEvent.Publisher {
    private final ImMessageTopic_StreamProducer producer;

    @Override
    public void publish(ImMessageCreatedEvent.Body body) {
        StreamPayload<ImMessageCreatedEvent.Body> payload = StreamPayload.<ImMessageCreatedEvent.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), body.getTenantId());
    }

}
