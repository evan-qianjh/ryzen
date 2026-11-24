package com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business;


import com.qianjh.ryzen.framework.stream.model.Stream;

public interface AccountBusiness<T> extends Stream<T> {
    String DOMAIN = "account_business";

    @Override
    default String getDomain() {
        return DOMAIN;
    }
}
