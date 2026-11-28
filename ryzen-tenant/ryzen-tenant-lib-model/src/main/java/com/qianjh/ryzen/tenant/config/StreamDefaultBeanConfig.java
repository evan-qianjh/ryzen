package com.qianjh.ryzen.tenant.config;

import com.qianjh.ryzen.tenant.stream.from_ims.push_conversation.PushConversationDispatched;
import com.qianjh.ryzen.tenant.stream.from_ims.push_message.PushMessageInsert;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class StreamDefaultBeanConfig {

    @Bean
    @ConditionalOnMissingBean(PushMessageInsert.Publisher.class)
    public PushMessageInsert.Publisher pushMessageInsertPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "PushMessageInsert.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(PushMessageInsert.Subscriber.class)
    public PushMessageInsert.Subscriber pushMessageInsertSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "PushMessageInsert.Subscriber is not configured"
            );
        };
    }


    @Bean
    @ConditionalOnMissingBean(PushConversationDispatched.Publisher.class)
    public PushConversationDispatched.Publisher pushConversationDispatchPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "PushConversationDispatch.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(PushConversationDispatched.Subscriber.class)
    public PushConversationDispatched.Subscriber pushConversationDispatchSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "PushConversationDispatch.Subscriber is not configured"
            );
        };
    }
}
