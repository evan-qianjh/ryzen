package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.im_conversation.im_conversation_dispatched.ImConversationDispatchedCreated;
import com.qianjh.ryzen.stream.producer.ImConversationTopic_StreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImConversationDispatchedCreatedEvent_PublisherImpl implements ImConversationDispatchedCreated.Publisher {
    private final ImConversationTopic_StreamProducer producer;

    @Override
    public void publish(ImConversationDispatchedCreated.Body body) {
        StreamPayload<ImConversationDispatchedCreated.Body> payload = StreamPayload.<ImConversationDispatchedCreated.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), body.getTenantId());
    }

}
