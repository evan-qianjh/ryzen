package com.qianjh.ryzen.ims.stream.app_to_scheduler;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface AppToSchedulerTask<T> extends Stream<T> {
    String DOMAIN = "APP_TO_SCHEDULER_TASK";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
