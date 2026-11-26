package com.qianjh.ryzen.ims.stream.im_conversation;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface ImConversationDispatched<T> extends Stream<T> {
    String DOMAIN = "im_conversation_dispatched";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
