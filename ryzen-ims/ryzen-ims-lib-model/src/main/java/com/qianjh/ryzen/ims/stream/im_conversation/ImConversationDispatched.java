package com.qianjh.ryzen.ims.stream.im_conversation;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface ImConversationDispatched<T> extends Stream<T> {
    String DOMAIN = "IM_CONVERSATION_DISPATCHED";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
