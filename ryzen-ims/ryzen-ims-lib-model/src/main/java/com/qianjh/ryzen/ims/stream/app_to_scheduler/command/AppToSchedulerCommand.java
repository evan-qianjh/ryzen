package com.qianjh.ryzen.ims.stream.app_to_scheduler.command;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface AppToSchedulerCommand<T> extends Stream<T> {
    String DOMAIN = "command";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
