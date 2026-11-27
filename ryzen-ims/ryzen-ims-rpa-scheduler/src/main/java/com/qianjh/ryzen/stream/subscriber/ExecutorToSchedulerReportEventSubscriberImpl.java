package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportEvent;
import com.qianjh.ryzen.service.ExecutorRegistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorToSchedulerReportEventSubscriberImpl implements ExecutorToSchedulerReportEvent.Subscriber {

    private final ExecutorRegistryService executorRegistryService;

    @Override
    public void subscribe(ExecutorToSchedulerReportEvent.Body body) {
        log.debug("消费 ExecutorToSchedulerReportEvent ::: {}", body);

        executorRegistryService.status(body);
    }
}
