package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.type.SchedulerToAppReportMessage;
import com.qianjh.ryzen.stream.producer.SchedulerToAppStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerToAppReportMessagePublisherImpl implements SchedulerToAppReportMessage.Publisher {
    private final SchedulerToAppStreamProducer producer;

    @Override
    public void publish(SchedulerToAppReportMessage.Body body) {
        StreamPayload<SchedulerToAppReportMessage.Body> payload = StreamPayload.<SchedulerToAppReportMessage.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, body.conversationCode(), null);
    }
}
