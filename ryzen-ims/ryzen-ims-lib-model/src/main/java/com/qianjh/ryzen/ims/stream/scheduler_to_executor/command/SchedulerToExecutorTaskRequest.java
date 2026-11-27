package com.qianjh.ryzen.ims.stream.scheduler_to_executor.command;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.SchedulerToExecutorTask;
import lombok.Builder;

/**
 * 账户创建事件
 *
 * @param <T>
 * @author QianJH
 */
public interface SchedulerToExecutorTaskRequest<T> extends SchedulerToExecutorTask<T> {
    String TYPE = "REQUEST";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            Long taskId,
            Long assignmentId,
            String executorCode,
            String conversationCode,
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
