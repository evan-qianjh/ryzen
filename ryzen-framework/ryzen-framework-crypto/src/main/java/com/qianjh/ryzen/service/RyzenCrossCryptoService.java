package com.qianjh.ryzen.service;

import java.time.Duration;

/**
 * 跨系统加密服务
 * @author QianJH
 */
public interface RyzenCrossCryptoService extends RsaCryptoService {

    /**
     * 加密
     *
     * @param plainText 明文
     * @return 密文
     */
    String encrypt(String plainText);

    /**
     * 加密
     *
     * @param plainText 明文
     * @param expire    过期时间
     * @return 密文
     */
    String encrypt(String plainText, Duration expire);

    /**
     * 加密对象
     *
     * @param obj 对象
     * @return 字符串
     */
    String encryptObj(Object obj);

    /**
     * 解密
     *
     * @param cipherText 密文
     * @return 明文
     */
    String decrypt(String cipherText);

    /**
     * 解密
     *
     * @param cipherText 密文
     * @param clazz      class
     * @param <T>        范型
     * @return 实体
     */
    <T> T decrypt(String cipherText, Class<T> clazz);
}
