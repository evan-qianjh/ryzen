package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.stream.producer.AccountTopic_StreamProducer;
import com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business.type.AccountBusinessCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountBusinessCreated_PublisherImpl implements AccountBusinessCreated.Publisher {
    private final AccountTopic_StreamProducer producer;

    @Override
    public void publish(AccountBusinessCreated.Body body) {
        StreamPayload<AccountBusinessCreated.Body> payload = StreamPayload.<AccountBusinessCreated.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.id()), body.oemId());
    }

}
