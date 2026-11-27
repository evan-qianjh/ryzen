package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportMessage;
import com.qianjh.ryzen.service.CollectedMessageService;
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
public class ExecutorToSchedulerReportMessageSubscriberImpl implements ExecutorToSchedulerReportMessage.Subscriber {

    private final CollectedMessageService collectedMessageService;

    @Override
    public void subscribe(ExecutorToSchedulerReportMessage.Body body) {
        log.debug("消费 ExecutorToSchedulerReportMessage ::: {}", body);

        collectedMessageService.forward(body);
    }
}
