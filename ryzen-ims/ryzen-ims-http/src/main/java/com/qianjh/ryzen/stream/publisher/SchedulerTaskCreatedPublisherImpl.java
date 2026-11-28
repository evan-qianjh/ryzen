package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.ims.stream.scheduler.scheduler_task.SchedulerTaskCreated;
import com.qianjh.ryzen.stream.producer.ImMessageStreamProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulerTaskCreatedPublisherImpl implements SchedulerTaskCreated.Publisher {
    private final ImMessageStreamProducer producer;

    @Override
    public void publish(SchedulerTaskCreated.Body body) {
        StreamPayload<SchedulerTaskCreated.Body> payload = StreamPayload.<SchedulerTaskCreated.Body>builder()
                .domain(getDomain())
                .type(getType())
                .body(body)
                .build();
        producer.publish(payload, IdUtils.toString(body.getId()), null);
    }
}
