package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerTaskResponse;
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
public class ExecutorToSchedulerTaskResponseSubscriberImpl implements ExecutorToSchedulerTaskResponse.Subscriber {

    private final SchedulerTaskService schedulerTaskService;

    @Override
    public void subscribe(ExecutorToSchedulerTaskResponse.Body body) {
        log.debug("消费 ExecutorToSchedulerTaskResponse ::: {}", body);
        schedulerTaskService.handleTaskResponse(body);
    }
}
