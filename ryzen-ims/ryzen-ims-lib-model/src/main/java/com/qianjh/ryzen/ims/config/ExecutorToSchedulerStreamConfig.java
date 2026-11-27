package com.qianjh.ryzen.ims.config;

import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ExecutorToSchedulerStreamConfig {

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportEvent.Publisher.class)
    public ExecutorToSchedulerReportEvent.Publisher executorToSchedulerReportEventPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportEvent.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportEvent.Subscriber.class)
    public ExecutorToSchedulerReportEvent.Subscriber executorToSchedulerReportEventSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportEvent.Subscriber is not configured"
            );
        };
    }

    /* ---------------------------------------------------------------------------------------- */

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportHeartbeat.Publisher.class)
    public ExecutorToSchedulerReportHeartbeat.Publisher executorToSchedulerReportHeartbeatPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportHeartbeat.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportHeartbeat.Subscriber.class)
    public ExecutorToSchedulerReportHeartbeat.Subscriber executorToSchedulerReportHeartbeatSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportHeartbeat.Subscriber is not configured"
            );
        };
    }


    /*----------------------------------------------------------------------------------------*/

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportMessage.Publisher.class)
    public ExecutorToSchedulerReportMessage.Publisher executorToSchedulerReportMessagePublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportMessage.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportMessage.Subscriber.class)
    public ExecutorToSchedulerReportMessage.Subscriber executorToSchedulerReportMessageSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportMessage.Subscriber is not configured"
            );
        };
    }

    /*----------------------------------------------------------------------------------------*/

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportRegister.Publisher.class)
    public ExecutorToSchedulerReportRegister.Publisher executorToSchedulerReportRegisterPublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportRegister.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerReportRegister.Subscriber.class)
    public ExecutorToSchedulerReportRegister.Subscriber executorToSchedulerReportRegisterSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerReportRegister.Subscriber is not configured"
            );
        };
    }

    /*----------------------------------------------------------------------------------------*/

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerCommandResponse.Publisher.class)
    public ExecutorToSchedulerCommandResponse.Publisher executorToSchedulerCommandResponsePublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerCommandResponse.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerCommandResponse.Subscriber.class)
    public ExecutorToSchedulerCommandResponse.Subscriber executorToSchedulerCommandResponseSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerCommandResponse.Subscriber is not configured"
            );
        };
    }

    /*----------------------------------------------------------------------------------------*/
    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerTaskResponse.Publisher.class)
    public ExecutorToSchedulerTaskResponse.Publisher executorToSchedulerTaskResponsePublisher() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerTaskResponse.Publisher is not configured"
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean(ExecutorToSchedulerTaskResponse.Subscriber.class)
    public ExecutorToSchedulerTaskResponse.Subscriber executorToSchedulerTaskResponseSubscriber() {
        return _ -> {
            throw new UnsupportedOperationException(
                    "ExecutorToSchedulerTaskResponse.Subscriber is not configured"
            );
        };
    }

    /*----------------------------------------------------------------------------------------*/
}
