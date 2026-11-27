package com.qianjh.ryzen.ims.stream.scheduler_to_app;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToAppTask<T> extends Stream<T> {
    String DOMAIN = "SCHEDULER_TO_APP_TASK";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
