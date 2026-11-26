package com.qianjh.ryzen.ims.stream.topic.executor_to_scheduler.domain.report.type;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.MessageDirection;
import com.qianjh.ryzen.ims.stream.topic.executor_to_scheduler.domain.report.ExecutorToSchedulerReport;
import lombok.Builder;

/**
 *
 * @param <T>
 * @author QianJH
 */
public interface ExecutorToSchedulerReportMessage<T> extends ExecutorToSchedulerReport<T> {
    String TYPE = "message";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            ImType imType,
            MessageDirection direction,
            String executorCode,
            String accountCode,
            String messageCode,
            String conversationCode,
            String conversationTitle,
            String conversationSubTitle,
            String conversationAvatar,
            String senderCode,
            String senderName,
            String senderAvatar,
            Long sentTime,
            String content
    ) {
    }

    /**
     * 发布者
     */
    interface Publisher extends StreamPublisher<Body> {
        @Override
        default String getDomain() {
            return DOMAIN;
        }

        @Override
        default String getType() {
            return TYPE;
        }

        @Override
        default Class<Body> getBodyClass() {
            return Body.class;
        }

        void publish(Body body);
    }

    /**
     * 订阅者
     */
    interface Subscriber extends StreamSubscriber<Body> {
        @Override
        default String getDomain() {
            return DOMAIN;
        }

        @Override
        default String getType() {
            return TYPE;
        }

        @Override
        default Class<Body> getBodyClass() {
            return Body.class;
        }
    }
}
