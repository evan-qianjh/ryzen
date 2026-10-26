package com.qianjh.ryzen.stream.domain.account.event.impl;

import com.qianjh.ryzen.user.stream.domain.account.event.AccountCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCreatedEvent_SubscriberImpl implements AccountCreatedEvent.Subscriber {

    @Override
    public void subscribe(AccountCreatedEvent.Body body) {
        log.info("消费 AccountCreatedEvent ::: {}", body);
        // TODO
    }
}
