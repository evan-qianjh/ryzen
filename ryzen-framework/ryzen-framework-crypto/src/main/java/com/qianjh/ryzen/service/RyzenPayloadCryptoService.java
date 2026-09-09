package com.qianjh.ryzen.service;

/**
 * 通讯加密服务
 * @author QianJH
 */
public interface RyzenPayloadCryptoService extends RsaCryptoService {

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return 密文
     */
    String encrypt(String plaintext);

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 明文
     */
    String decrypt(String ciphertext);

    /**
     * 解密
     *
     * @param ciphertext   密文
     * @param encryptKeyId 密钥ID
     * @return 铭文
     */
    String decrypt(String ciphertext, Long encryptKeyId);
}
