package com.qianjh.ryzen.tenant.stream.from_ims;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface PushConversation<T> extends Stream<T> {
    String DOMAIN = "PUSH_CONVERSATION";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
