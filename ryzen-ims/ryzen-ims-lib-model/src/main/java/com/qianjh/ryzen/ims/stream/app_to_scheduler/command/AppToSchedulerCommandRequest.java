package com.qianjh.ryzen.ims.stream.app_to_scheduler.command;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.stream.app_to_scheduler.AppToSchedulerCommand;
import lombok.Builder;

/**
 * 账户创建事件
 *
 * @param <T>
 * @author QianJH
 */
public interface AppToSchedulerCommandRequest<T> extends AppToSchedulerCommand<T> {
    String TYPE = "REQUEST";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            String code,
            SchedulerCommandType type,
            // type.assign用到
            ImType imType,
            String accountCode,
            // type.release/start/stop用到
            Long assignmentId
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
