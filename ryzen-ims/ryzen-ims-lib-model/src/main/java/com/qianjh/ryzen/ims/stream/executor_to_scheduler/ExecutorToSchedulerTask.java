package com.qianjh.ryzen.ims.stream.executor_to_scheduler;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface ExecutorToSchedulerTask<T> extends Stream<T> {
    String DOMAIN = "EXECUTOR_TO_SCHEDULER_TASK";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
