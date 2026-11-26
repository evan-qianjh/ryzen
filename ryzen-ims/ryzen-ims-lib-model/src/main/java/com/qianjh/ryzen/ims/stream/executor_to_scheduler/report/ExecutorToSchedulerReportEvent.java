package com.qianjh.ryzen.ims.stream.executor_to_scheduler.report;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerReportEventType;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.ExecutorToSchedulerReport;
import lombok.Builder;

/**
 *
 * @param <T>
 * @author QianJH
 */
public interface ExecutorToSchedulerReportEvent<T> extends ExecutorToSchedulerReport<T> {
    String TYPE = "event";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            String executorCode,
            Long assignmentId,

            ExecutorToSchedulerReportEventType type
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
