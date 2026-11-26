package com.qianjh.ryzen.ims.stream.domain.command;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface SchedulerToAppCommand<T> extends Stream<T> {
    String DOMAIN = "command";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
