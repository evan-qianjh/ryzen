package com.qianjh.ryzen.service;

import com.qianjh.ryzen.service.dto.RsaEncrypt;

import java.time.Duration;

/**
 * 跨系统加密服务
 *
 * @author QianJH
 */
public interface RyzenCrossService extends RsaService {

    /**
     * 加密
     *
     * @param plainText 明文
     * @return 密文
     */
    RsaEncrypt encrypt(String plainText);

    /**
     * 加密
     *
     * @param plainText 明文
     * @param expire    过期时间
     * @return 密文
     */
    RsaEncrypt encrypt(String plainText, Duration expire);

    /**
     * 加密对象
     *
     * @param obj 对象
     * @return 字符串
     */
    RsaEncrypt encryptObj(Object obj);

    /**
     * 解密
     *
     * @param keyId      密钥ID
     * @param cipherText 密文
     * @return 明文
     */
    String decrypt(Long keyId, String cipherText);

    /**
     * 解密
     *
     * @param keyId      密钥ID
     * @param cipherText 密文
     * @param clazz      class
     * @param <T>        范型
     * @return 实体
     */
    <T> T decrypt(Long keyId, String cipherText, Class<T> clazz);
}
