package com.qianjh.ryzen.topic._private.ims_message;


import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushMessageInsert;

public interface ImsMessagePusher {

    /**
     *
     */
    void push(PushMessageInsert.Body payload);
}
