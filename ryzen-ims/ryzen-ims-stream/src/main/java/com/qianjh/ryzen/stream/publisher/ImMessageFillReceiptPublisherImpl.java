package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageFillReceipt;
import com.qianjh.ryzen.stream.producer.ImMessageStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImMessageFillReceiptPublisherImpl implements ImMessageFillReceipt.Publisher {
    private final ImMessageStreamProducer producer;

    @Override
    public void publish(ImMessageFillReceipt.Body body) {
        StreamPayload<ImMessageFillReceipt.Body> payload = StreamPayload.<ImMessageFillReceipt.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), body.getTenantId());
    }

}
