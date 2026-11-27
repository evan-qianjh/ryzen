package com.qianjh.ryzen.ims.stream.im_message;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface ImMessage<T> extends Stream<T> {
    String DOMAIN = "IM_MESSAGE";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
