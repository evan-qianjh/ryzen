package com.qianjh.ryzen.topic._private.ims_conversation;


import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation.type.PushConversationDispatched;

public interface ImsConversationPusher {
    /**
     *
     */
    void push(PushConversationDispatched.Body body);
}
