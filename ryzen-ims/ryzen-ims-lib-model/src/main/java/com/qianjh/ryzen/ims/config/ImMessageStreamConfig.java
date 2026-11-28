package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageCreated;
import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageFillReceipt;
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


    @Bean
    @ConditionalOnMissingBean(ImMessageFillReceipt.Publisher.class)
    public ImMessageFillReceipt.Publisher imMessageFillReceiptPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImMessageFillReceipt.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ImMessageFillReceipt.Subscriber.class)
    public ImMessageFillReceipt.Subscriber imMessageFillReceiptSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ImMessageFillReceipt.Subscriber is not configured"
            );
        };
    }

}
