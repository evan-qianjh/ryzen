package com.qianjh.ryzen.ims.stream.scheduler_to_app;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToAppReport<T> extends Stream<T> {
    String DOMAIN = "SCHEDULER_TO_APP_REPORT";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
