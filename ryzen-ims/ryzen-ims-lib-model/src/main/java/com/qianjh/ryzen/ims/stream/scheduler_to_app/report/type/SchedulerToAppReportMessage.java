package com.qianjh.ryzen.ims.stream.scheduler_to_app.report.type;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.MessageDirection;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.SchedulerToAppReport;
import lombok.Builder;

/**
 * 账户创建事件
 *
 * @param <T>
 * @author QianJH
 */
public interface SchedulerToAppReportMessage<T> extends SchedulerToAppReport<T> {
    String TYPE = "message";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            ImType imType,
            String accountCode,
            String messageCode,
            MessageDirection direction,

            String conversationCode,
            String conversationTitle,
            String conversationSubTitle,
            String conversationAvatar,
            String senderCode,
            String senderName,
            String senderAvatar,
            String content,
            Long sentTime
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
