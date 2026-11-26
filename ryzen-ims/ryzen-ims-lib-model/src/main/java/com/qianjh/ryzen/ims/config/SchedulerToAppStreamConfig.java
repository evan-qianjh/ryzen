package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.type.SchedulerToAppReportEvent;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.type.SchedulerToAppReportMessage;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.task.type.SchedulerToAppTaskResponse;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SchedulerToAppStreamConfig {

    @Bean
    @ConditionalOnMissingBean(SchedulerToAppReportEvent.Publisher.class)
    public SchedulerToAppReportEvent.Publisher schedulerToAppReportEventPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToAppReportEvent.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(SchedulerToAppReportEvent.Subscriber.class)
    public SchedulerToAppReportEvent.Subscriber schedulerToAppReportEventSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToAppReportEvent.Subscriber is not configured"
            );
        };
    }

    /* ---------------------------------------------------------------------------------------- */

    @Bean
    @ConditionalOnMissingBean(SchedulerToAppReportMessage.Publisher.class)
    public SchedulerToAppReportMessage.Publisher schedulerToAppReportMessagePublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToAppReportMessage.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(SchedulerToAppReportMessage.Subscriber.class)
    public SchedulerToAppReportMessage.Subscriber schedulerToAppReportMessageSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToAppReportMessage.Subscriber is not configured"
            );
        };
    }

    /* ---------------------------------------------------------------------------------------- */

    @Bean
    @ConditionalOnMissingBean(SchedulerToAppTaskResponse.Publisher.class)
    public SchedulerToAppTaskResponse.Publisher schedulerToAppTaskResponsePublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToAppTaskResponse.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(SchedulerToAppTaskResponse.Subscriber.class)
    public SchedulerToAppTaskResponse.Subscriber schedulerToAppTaskResponseSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "SchedulerToAppTaskResponse.Subscriber is not configured"
            );
        };
    }
}
