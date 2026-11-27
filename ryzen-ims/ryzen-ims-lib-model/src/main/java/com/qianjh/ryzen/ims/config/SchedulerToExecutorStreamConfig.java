package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.scheduler_to_executor.command.SchedulerToExecutorCommandRequest;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.command.SchedulerToExecutorTaskRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SchedulerToExecutorStreamConfig {

    @Bean
    @ConditionalOnMissingBean(SchedulerToExecutorCommandRequest.Publisher.class)
    public SchedulerToExecutorCommandRequest.Publisher schedulerToExecutorCommandRequestPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToExecutorCommandRequest.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(SchedulerToExecutorCommandRequest.Subscriber.class)
    public SchedulerToExecutorCommandRequest.Subscriber schedulerToExecutorCommandRequestSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToExecutorCommandRequest.Subscriber is not configured"
            );
        };
    }

    /* ---------------------------------------------------------------------------------------- */

    @Bean
    @ConditionalOnMissingBean(SchedulerToExecutorTaskRequest.Publisher.class)
    public SchedulerToExecutorTaskRequest.Publisher schedulerToExecutorTaskRequestPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToExecutorTaskRequest.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(SchedulerToExecutorTaskRequest.Subscriber.class)
    public SchedulerToExecutorTaskRequest.Subscriber schedulerToExecutorTaskRequestSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToExecutorTaskRequest.Subscriber is not configured"
            );
        };
    }
}
