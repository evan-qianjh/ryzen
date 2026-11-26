package com.qianjh.ryzen.ims.stream.executor_to_scheduler;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface ExecutorToSchedulerReport<T> extends Stream<T> {
    String DOMAIN = "report";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
