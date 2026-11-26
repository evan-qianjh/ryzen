package com.qianjh.ryzen.tenant.config;

import com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business.type.AccountBusinessCreated;
import com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business.type.AccountBusinessPatched;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation.type.PushConversationDispatched;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushMessageInsert;
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

    @Bean
    @ConditionalOnMissingBean(AccountBusinessCreated.Publisher.class)
    public AccountBusinessCreated.Publisher accountBusinessCreatedPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AccountBusinessCreated.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(AccountBusinessCreated.Subscriber.class)
    public AccountBusinessCreated.Subscriber accountBusinessCreatedSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AccountBusinessCreated.Subscriber is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(AccountBusinessPatched.Publisher.class)
    public AccountBusinessPatched.Publisher accountBusinessPatchedPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AccountBusinessPatched.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(AccountBusinessPatched.Subscriber.class)
    public AccountBusinessPatched.Subscriber accountBusinessPatchedSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AccountBusinessPatched.Subscriber is not configured"
            );
        };
    }
}
