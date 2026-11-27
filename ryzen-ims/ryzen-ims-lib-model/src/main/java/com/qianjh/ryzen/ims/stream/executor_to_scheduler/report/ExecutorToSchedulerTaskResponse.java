package com.qianjh.ryzen.ims.stream.executor_to_scheduler.report;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.enums.ExecutorCommandType;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerTaskResult;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.ExecutorToSchedulerTask;
import lombok.Builder;

/**
 *
 * @param <T>
 * @author QianJH
 */
public interface ExecutorToSchedulerTaskResponse<T> extends ExecutorToSchedulerTask<T> {
    String TYPE = "RESPONSE";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            Long assignmentId,
            String executorCode,
            Long taskId,
            ExecutorToSchedulerTaskResult result,
            String errorMessage,
            String receiptCode
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
