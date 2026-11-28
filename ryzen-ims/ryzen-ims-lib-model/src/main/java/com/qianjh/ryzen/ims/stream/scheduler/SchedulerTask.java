package com.qianjh.ryzen.ims.stream.scheduler;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerTask<T> extends Stream<T> {
    String DOMAIN = "SCHEDULER_TASK";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
