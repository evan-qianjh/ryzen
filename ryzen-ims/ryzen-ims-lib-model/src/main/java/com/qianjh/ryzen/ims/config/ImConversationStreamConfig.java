package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.im_conversation.im_conversation_dispatched.ImConversationDispatchedCreated;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ImConversationStreamConfig {

    @Bean
    @ConditionalOnMissingBean(ImConversationDispatchedCreated.Publisher.class)
    public ImConversationDispatchedCreated.Publisher imConversationDispatchedCreatedEventPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImConversationDispatchedCreatedEvent.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ImConversationDispatchedCreated.Subscriber.class)
    public ImConversationDispatchedCreated.Subscriber imConversationDispatchedCreatedEventSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImConversationDispatchedCreatedEvent.Subscriber is not configured"
            );
        };
    }


}
