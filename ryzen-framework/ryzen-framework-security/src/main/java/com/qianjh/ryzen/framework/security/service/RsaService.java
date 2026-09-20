package com.qianjh.ryzen.framework.security.service;

import com.qianjh.ryzen.framework.security.config.RsaProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author QianJH
 */
public interface RsaService {

    /**
     * 获取公钥
     *
     * @return 公钥
     */
    RSAPublicKey getPublicKey(RsaProperties properties);

    /**
     * 获取私钥
     *
     * @return 私钥
     */
    RSAPrivateKey getPrivateKey(RsaProperties properties);

    /**
     * 获取公钥
     *
     * @param keyId 密钥ID
     * @return 公钥
     */
    RSAPublicKey getPublicKey(Long keyId);

    /**
     * 获取私钥
     *
     * @param keyId 密钥ID
     * @return 私钥
     */
    RSAPrivateKey getPrivateKey(Long keyId);


}
