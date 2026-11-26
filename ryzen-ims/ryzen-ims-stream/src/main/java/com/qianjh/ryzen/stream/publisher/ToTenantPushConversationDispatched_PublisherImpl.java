package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.stream.producer.ToTenantTopic_StreamProducer;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation.type.PushConversationDispatched;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author QianJH
 */
@Component
@RequiredArgsConstructor
public class ToTenantPushConversationDispatched_PublisherImpl implements PushConversationDispatched.Publisher {
    private final ToTenantTopic_StreamProducer producer;

    @Override
    public void publish(PushConversationDispatched.Body body) {
        StreamPayload<PushConversationDispatched.Body> payload = StreamPayload.<PushConversationDispatched.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.id()), body.tenantId());
    }
}
