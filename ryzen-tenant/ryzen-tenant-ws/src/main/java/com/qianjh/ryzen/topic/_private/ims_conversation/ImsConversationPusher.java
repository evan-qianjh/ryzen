package com.qianjh.ryzen.topic._private.ims_conversation;


import com.qianjh.ryzen.tenant.stream.from_ims.push_conversation.PushConversationDispatched;

public interface ImsConversationPusher {
    /**
     *
     */
    void push(PushConversationDispatched.Body body);
}
