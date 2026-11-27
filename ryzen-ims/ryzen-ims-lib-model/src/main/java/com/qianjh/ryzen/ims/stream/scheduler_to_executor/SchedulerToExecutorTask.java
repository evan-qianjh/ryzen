package com.qianjh.ryzen.ims.stream.scheduler_to_executor;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToExecutorTask<T> extends Stream<T> {
    String DOMAIN = "SCHEDULER_TO_EXECUTOR_TASK";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
