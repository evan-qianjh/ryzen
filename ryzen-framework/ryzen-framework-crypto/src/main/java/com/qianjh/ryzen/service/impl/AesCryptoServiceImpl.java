package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.service.AesCryptoService;

/**
 *
 * @author QianJH
 */
public abstract class AesCryptoServiceImpl implements AesCryptoService {

    protected String secret;

    @Override
    public String getSecret() {
        return secret;
    }
}
