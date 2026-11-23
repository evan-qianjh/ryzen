package com.qianjh.ryzen.tenant.config;

import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushInsertMessage;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.impl.PushInsertMessage_DefaultPublisherImpl;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.impl.PushInsertMessage_DefaultSubscriberImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamDefaultBeanConfig {

    @Bean
    @ConditionalOnMissingBean(PushInsertMessage.Publisher.class)
    public PushInsertMessage.Publisher imMessageCreatedEventPublisher() {
        return new PushInsertMessage_DefaultPublisherImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PushInsertMessage.Subscriber.class)
    public PushInsertMessage.Subscriber imMessageCreatedEventSubscriber() {
        return new PushInsertMessage_DefaultSubscriberImpl();
    }
}
