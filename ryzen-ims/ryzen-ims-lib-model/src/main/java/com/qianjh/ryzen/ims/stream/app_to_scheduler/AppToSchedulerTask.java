package com.qianjh.ryzen.ims.stream.app_to_scheduler;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface AppToSchedulerTask<T> extends Stream<T> {
    String DOMAIN = "task";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
