package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportRegister;
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
public class ExecutorToSchedulerReportRegisterSubscriberImpl implements ExecutorToSchedulerReportRegister.Subscriber {

    private final ExecutorRegistryService executorRegistryService;

    @Override
    public void subscribe(ExecutorToSchedulerReportRegister.Body body) {
        log.debug("消费 ExecutorToSchedulerReportRegister ::: {}", body);

        executorRegistryService.register(body);
    }
}
