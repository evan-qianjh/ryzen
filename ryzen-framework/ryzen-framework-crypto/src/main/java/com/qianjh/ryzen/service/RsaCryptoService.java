package com.qianjh.ryzen.service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author QianJH
 */
public interface RsaCryptoService {

    Long getKeyId();

    /**
     * 获取公钥
     *
     * @return 公钥
     */
    RSAPublicKey getPublicKey();

    /**
     * 获取私钥
     *
     * @return 私钥
     */
    RSAPrivateKey getPrivateKey();

}
