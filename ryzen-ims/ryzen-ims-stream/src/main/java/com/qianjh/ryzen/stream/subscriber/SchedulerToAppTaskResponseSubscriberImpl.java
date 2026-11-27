package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.scheduler_to_app.task.SchedulerToAppTaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerToAppTaskResponseSubscriberImpl implements SchedulerToAppTaskResponse.Subscriber {


    @Override
    public void subscribe(SchedulerToAppTaskResponse.Body body) {
        log.info("消费 SchedulerToAppTaskResponse ::: {}", body);
        // TODO

    }
}
