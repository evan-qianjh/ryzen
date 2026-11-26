package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageCreated;
import com.qianjh.ryzen.stream.producer.ImMessageTopic_StreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImMessageCreatedEvent_PublisherImpl implements ImMessageCreated.Publisher {
    private final ImMessageTopic_StreamProducer producer;

    @Override
    public void publish(ImMessageCreated.Body body) {
        StreamPayload<ImMessageCreated.Body> payload = StreamPayload.<ImMessageCreated.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), body.getTenantId());
    }

}
