package com.qianjh.ryzen.topic._private.ims_message;


import com.qianjh.ryzen.entity.ImMessage;

public interface ImsMessagePusher {

    /**
     *
     */
    void push(ImMessage message);
}
