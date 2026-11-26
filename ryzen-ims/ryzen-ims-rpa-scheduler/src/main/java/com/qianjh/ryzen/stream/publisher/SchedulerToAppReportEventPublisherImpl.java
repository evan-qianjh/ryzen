package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.domain.report.type.SchedulerToAppReportEvent;
import com.qianjh.ryzen.stream.producer.SchedulerToAppStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerToAppReportEventPublisherImpl implements SchedulerToAppReportEvent.Publisher {
    private final SchedulerToAppStreamProducer producer;

    @Override
    public void publish(SchedulerToAppReportEvent.Body body) {
        StreamPayload<SchedulerToAppReportEvent.Body> payload = StreamPayload.<SchedulerToAppReportEvent.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.assignmentId()), null);
    }
}
