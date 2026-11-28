package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.stream.producer.ToTenantTopic_StreamProducer;
import com.qianjh.ryzen.tenant.stream.from_ims.push_message.PushMessageInsert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author QianJH
 */
@Component
@RequiredArgsConstructor
public class PushMessageInsertPublisherImpl implements PushMessageInsert.Publisher {
    private final ToTenantTopic_StreamProducer producer;

    @Override
    public void publish(PushMessageInsert.Body body) {
        StreamPayload<PushMessageInsert.Body> payload = StreamPayload.<PushMessageInsert.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.id()), body.tenantId());
    }
}
