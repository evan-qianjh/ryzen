package com.qianjh.ryzen.topic._private.ims_message;


import com.qianjh.ryzen.tenant.stream.from_ims.push_message.PushMessageInsert;

public interface ImsMessagePusher {

    /**
     *
     */
    void push(PushMessageInsert.Body payload);
}
