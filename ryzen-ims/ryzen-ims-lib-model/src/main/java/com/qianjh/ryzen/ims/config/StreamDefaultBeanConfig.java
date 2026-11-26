package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.domain.command.type.AppToSchedulerCommandRequest;
import com.qianjh.ryzen.ims.stream.domain.im_conversation_dispatched.type.ImConversationDispatchedCreatedEvent;
import com.qianjh.ryzen.ims.stream.domain.im_message.type.ImMessageCreatedEvent;
import com.qianjh.ryzen.ims.stream.domain.task.type.AppToSchedulerTaskRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
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


    @Bean
    @ConditionalOnMissingBean(AppToSchedulerCommandRequest.Publisher.class)
    public AppToSchedulerCommandRequest.Publisher appToSchedulerCommandRequestPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AppToSchedulerCommandRequest.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(AppToSchedulerCommandRequest.Subscriber.class)
    public AppToSchedulerCommandRequest.Subscriber appToSchedulerCommandRequestSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AppToSchedulerCommandRequest.Subscriber is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(AppToSchedulerTaskRequest.Publisher.class)
    public AppToSchedulerTaskRequest.Publisher appToSchedulerTaskRequestPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AppToSchedulerTaskRequest.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(AppToSchedulerTaskRequest.Subscriber.class)
    public AppToSchedulerTaskRequest.Subscriber appToSchedulerTaskRequestSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "AppToSchedulerTaskRequest.Subscriber is not configured"
            );
        };
    }
}
