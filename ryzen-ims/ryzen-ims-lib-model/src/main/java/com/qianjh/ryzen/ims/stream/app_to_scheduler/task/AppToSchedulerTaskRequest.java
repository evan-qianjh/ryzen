package com.qianjh.ryzen.ims.stream.app_to_scheduler.task;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.AppToSchedulerTask;
import lombok.Builder;

/**
 * 账户创建事件
 *
 * @param <T>
 * @author QianJH
 */
public interface AppToSchedulerTaskRequest<T> extends AppToSchedulerTask<T> {
    String TYPE = "request";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            String code,
            Long assignmentId,
            String conversationCode,
            String content,
            Long deadlineTime
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
