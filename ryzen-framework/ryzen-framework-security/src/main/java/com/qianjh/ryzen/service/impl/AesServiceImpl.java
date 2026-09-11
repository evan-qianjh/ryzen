package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.service.AesService;

/**
 *
 * @author QianJH
 */
public abstract class AesServiceImpl implements AesService {

    protected String secret;

    @Override
    public String getSecret() {
        return secret;
    }
}
