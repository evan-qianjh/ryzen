package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.scheduler.scheduler_task.SchedulerTaskCreated;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SchedulerStreamConfig {

    @Bean
    @ConditionalOnMissingBean(SchedulerTaskCreated.Publisher.class)
    public SchedulerTaskCreated.Publisher schedulerTaskCreatedPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerTaskCreated.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(SchedulerTaskCreated.Subscriber.class)
    public SchedulerTaskCreated.Subscriber schedulerTaskCreatedSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerTaskCreated.Subscriber is not configured"
            );
        };
    }
}
