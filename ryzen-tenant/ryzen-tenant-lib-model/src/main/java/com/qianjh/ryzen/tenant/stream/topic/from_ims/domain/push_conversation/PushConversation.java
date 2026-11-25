package com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface PushConversation<T> extends Stream<T> {
    String DOMAIN = "push_conversation";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
