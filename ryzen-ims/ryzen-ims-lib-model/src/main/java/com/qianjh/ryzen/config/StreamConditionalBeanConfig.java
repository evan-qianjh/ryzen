package com.qianjh.ryzen.config;

import com.qianjh.ryzen.ims.stream.domain.im_message.event.ImMessageCreatedEvent;
import com.qianjh.ryzen.ims.stream.domain.im_message.event.impl.ImMessageCreatedEvent_DefaultPublisherImpl;
import com.qianjh.ryzen.ims.stream.domain.im_message.event.impl.ImMessageCreatedEvent_DefaultSubscriberImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamConditionalBeanConfig {

    @Bean
    @ConditionalOnMissingBean(ImMessageCreatedEvent.Publisher.class)
    public ImMessageCreatedEvent.Publisher imMessageCreatedEventPublisher() {
        return new ImMessageCreatedEvent_DefaultPublisherImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ImMessageCreatedEvent.Subscriber.class)
    public ImMessageCreatedEvent.Subscriber imMessageCreatedEventSubscriber() {
        return new ImMessageCreatedEvent_DefaultSubscriberImpl();
    }
}
