package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerCommandResponse;
import com.qianjh.ryzen.service.ExecutorCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorToSchedulerCommandResponseSubscriberImpl implements ExecutorToSchedulerCommandResponse.Subscriber {


    private final ExecutorCommandService executorCommandService;

    @Override
    public void subscribe(ExecutorToSchedulerCommandResponse.Body body) {
        log.info("消费 ExecutorToSchedulerCommandResponse ::: {}", body);
        executorCommandService.handle(body);
    }
}
