package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.domain.im_conversation_dispatched.type.ImConversationDispatchedCreatedEvent;
import com.qianjh.ryzen.stream.producer.ImConversationTopic_StreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImConversationDispatchedCreatedEvent_PublisherImpl implements ImConversationDispatchedCreatedEvent.Publisher {
    private final ImConversationTopic_StreamProducer producer;

    @Override
    public void publish(ImConversationDispatchedCreatedEvent.Body body) {
        StreamPayload<ImConversationDispatchedCreatedEvent.Body> payload = StreamPayload.<ImConversationDispatchedCreatedEvent.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), body.getTenantId());
    }

}
