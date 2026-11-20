package com.qianjh.ryzen.ims.stream.domain.im_message;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface ImMessageEvent<T> extends Stream<T> {
    String DOMAIN = "im_message";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
