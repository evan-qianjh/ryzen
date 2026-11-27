package com.qianjh.ryzen.ims.stream.scheduler_to_app;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToAppCommand<T> extends Stream<T> {
    String DOMAIN = "SCHEDULER_TO_APP_COMMAND";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
