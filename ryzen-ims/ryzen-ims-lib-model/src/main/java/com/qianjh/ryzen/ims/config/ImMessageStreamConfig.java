package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageCreated;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ImMessageStreamConfig {

    @Bean
    @ConditionalOnMissingBean(ImMessageCreated.Publisher.class)
    public ImMessageCreated.Publisher imMessageCreatedEventPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImMessageCreatedEvent.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ImMessageCreated.Subscriber.class)
    public ImMessageCreated.Subscriber imMessageCreatedEventSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImMessageCreatedEvent.Subscriber is not configured"
            );
        };
    }

}
