package com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface PushMessage<T> extends Stream<T> {
    String DOMAIN = "push_message";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
