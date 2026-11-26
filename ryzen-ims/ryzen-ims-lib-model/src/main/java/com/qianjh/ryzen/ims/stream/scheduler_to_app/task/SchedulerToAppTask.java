package com.qianjh.ryzen.ims.stream.scheduler_to_app.task;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToAppTask<T> extends Stream<T> {
    String DOMAIN = "task";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
