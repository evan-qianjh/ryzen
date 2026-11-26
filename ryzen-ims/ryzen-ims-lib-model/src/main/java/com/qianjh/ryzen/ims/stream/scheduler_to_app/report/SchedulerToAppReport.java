package com.qianjh.ryzen.ims.stream.scheduler_to_app.report;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToAppReport<T> extends Stream<T> {
    String DOMAIN = "report";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
