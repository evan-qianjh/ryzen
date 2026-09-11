package com.qianjh.ryzen.service;

import com.qianjh.ryzen.service.dto.RsaEncrypt;

/**
 * 通讯加密服务
 *
 * @author QianJH
 */
public interface RyzenMessageService extends RsaService {

    /**
     * 加密
     *
     * @param plaintext 明文
     * @return 密文
     */
    RsaEncrypt encrypt(String plaintext);

    /**
     * 解密
     *
     * @param keyId      密钥ID
     * @param ciphertext 密文
     * @return 铭文
     */
    String decrypt(Long keyId, String ciphertext);
}
