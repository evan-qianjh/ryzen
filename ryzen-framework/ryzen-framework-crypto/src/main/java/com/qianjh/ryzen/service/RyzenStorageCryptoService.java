package com.qianjh.ryzen.service;

/**
 * @author QianJH
 */
public interface RyzenStorageCryptoService extends AesCryptoService {

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
}
