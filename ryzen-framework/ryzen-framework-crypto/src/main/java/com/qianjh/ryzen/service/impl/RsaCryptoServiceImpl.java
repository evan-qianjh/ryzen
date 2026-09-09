package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.service.RsaCryptoService;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 *
 * @author QianJH
 */
public abstract class RsaCryptoServiceImpl implements RsaCryptoService {
    protected Long keyId;
    protected RSAPublicKey rsaPublicKey;
    protected RSAPrivateKey rsaPrivateKey;

    @Override
    public Long getKeyId() {
        return keyId;
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return rsaPublicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return rsaPrivateKey;
    }
}
