package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.domain.im_conversation_dispatched.type.ImConversationDispatchedCreatedEvent;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ImConversationStreamConfig {

    @Bean
    @ConditionalOnMissingBean(ImConversationDispatchedCreatedEvent.Publisher.class)
    public ImConversationDispatchedCreatedEvent.Publisher imConversationDispatchedCreatedEventPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImConversationDispatchedCreatedEvent.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ImConversationDispatchedCreatedEvent.Subscriber.class)
    public ImConversationDispatchedCreatedEvent.Subscriber imConversationDispatchedCreatedEventSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImConversationDispatchedCreatedEvent.Subscriber is not configured"
            );
        };
    }


}
