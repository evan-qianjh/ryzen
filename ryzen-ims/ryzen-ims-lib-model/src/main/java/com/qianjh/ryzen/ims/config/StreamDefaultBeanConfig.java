package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.domain.im_message.event.ImMessageCreatedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamDefaultBeanConfig {

    @Bean
    @ConditionalOnMissingBean(ImMessageCreatedEvent.Publisher.class)
    public ImMessageCreatedEvent.Publisher imMessageCreatedEventPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImMessageCreatedEvent.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ImMessageCreatedEvent.Subscriber.class)
    public ImMessageCreatedEvent.Subscriber imMessageCreatedEventSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImMessageCreatedEvent.Subscriber is not configured"
            );
        };
    }
}
