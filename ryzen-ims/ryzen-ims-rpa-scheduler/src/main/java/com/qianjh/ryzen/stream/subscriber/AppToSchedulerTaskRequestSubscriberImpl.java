package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.app_to_scheduler.task.AppToSchedulerTaskRequest;
import com.qianjh.ryzen.service.SchedulerTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppToSchedulerTaskRequestSubscriberImpl implements AppToSchedulerTaskRequest.Subscriber {

    private final SchedulerTaskService schedulerTaskService;

    @Override
    public void subscribe(AppToSchedulerTaskRequest.Body body) {
        log.debug("AppToSchedulerTaskRequest ::: {}", body);

        schedulerTaskService.accept(body);
    }
}
