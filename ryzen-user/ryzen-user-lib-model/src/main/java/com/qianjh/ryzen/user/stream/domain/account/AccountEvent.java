package com.qianjh.ryzen.user.stream.domain.account;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface AccountEvent<T> extends Stream<T> {
    String DOMAIN = "account";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
