package com.qianjh.ryzen.ims.stream.scheduler_to_executor.command;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.dto.Assignment;
import com.qianjh.ryzen.ims.enums.ExecutorCommandType;
import com.qianjh.ryzen.ims.stream.scheduler_to_executor.SchedulerToExecutorCommand;
import lombok.Builder;

/**
 * 账户创建事件
 *
 * @param <T>
 * @author QianJH
 */
public interface SchedulerToExecutorCommandRequest<T> extends SchedulerToExecutorCommand<T> {
    String TYPE = "REQUEST";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            String code,
            ExecutorCommandType type,
            String executorCode,
            Assignment assignment
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
