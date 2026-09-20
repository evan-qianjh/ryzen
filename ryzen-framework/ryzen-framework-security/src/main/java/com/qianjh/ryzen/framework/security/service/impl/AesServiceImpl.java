package com.qianjh.ryzen.framework.security.service.impl;

import com.qianjh.ryzen.framework.security.service.AesService;

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
