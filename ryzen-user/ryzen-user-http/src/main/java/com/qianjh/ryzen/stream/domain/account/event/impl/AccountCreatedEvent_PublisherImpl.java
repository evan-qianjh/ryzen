package com.qianjh.ryzen.stream.domain.account.event.impl;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.stream.topic.user.account.UserAccountTopic_StreamProducer;
import com.qianjh.ryzen.user.stream.domain.account.event.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountCreatedEvent_PublisherImpl implements AccountCreatedEvent.Publisher {
    private final UserAccountTopic_StreamProducer producer;

    @Override
    public void publish(AccountCreatedEvent.Body body) {
        StreamPayload<AccountCreatedEvent.Body> payload = StreamPayload.<AccountCreatedEvent.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), body.getTenantId());
    }

}
