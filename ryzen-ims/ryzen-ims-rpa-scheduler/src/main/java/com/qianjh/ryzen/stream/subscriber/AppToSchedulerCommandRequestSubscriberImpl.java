package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.domain.command.type.AppToSchedulerCommandRequest;
import com.qianjh.ryzen.service.SchedulerCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppToSchedulerCommandRequestSubscriberImpl implements AppToSchedulerCommandRequest.Subscriber {

    private final SchedulerCommandService schedulerCommandService;

    @Override
    public void subscribe(AppToSchedulerCommandRequest.Body body) {
        log.debug("消费AppToSchedulerCommandRequest ::: {}", body);

        schedulerCommandService.handle(body);
    }
}
