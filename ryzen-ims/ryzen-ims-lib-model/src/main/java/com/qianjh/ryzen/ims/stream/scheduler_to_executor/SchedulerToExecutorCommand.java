package com.qianjh.ryzen.ims.stream.scheduler_to_executor;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToExecutorCommand<T> extends Stream<T> {
    String DOMAIN = "SCHEDULER_TO_EXECUTOR_COMMAND";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
