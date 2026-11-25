package com.qianjh.ryzen.topic._private.ims_conversation;


import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation.type.PushConversationDispatch;

public interface ImsConversationPusher {
    /**
     *
     */
    void push(PushConversationDispatch.Body body);
}
