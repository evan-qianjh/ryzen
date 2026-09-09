package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.api.RespMc;
import com.qianjh.ryzen.config.PayloadProperties;
import com.qianjh.ryzen.service.RyzenPayloadCryptoService;
import com.qianjh.ryzen.util.McUtils;
import com.qianjh.ryzen.util.RSAUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RyzenPayloadCryptoServiceImpl extends RsaCryptoServiceImpl implements RyzenPayloadCryptoService {

    private final PayloadProperties payloadProperties;

    @PostConstruct
    public void init() {
        keyId = payloadProperties.getKeyId();
        rsaPrivateKey = RSAUtils.buildRsaPrivateKey(payloadProperties.getRsaPrivateKey());
        rsaPublicKey = RSAUtils.buildRsaPublicKey(payloadProperties.getRsaPublicKey());
    }

    @Override
    public String encrypt(String plaintext) {
        if (StringUtils.isBlank(plaintext)) {
            return null;
        }
        return RSAUtils.encrypt(rsaPublicKey, plaintext);
    }

    @Override
    public String decrypt(String ciphertext) {
        return decrypt(ciphertext, keyId);
    }

    @Override
    public String decrypt(String ciphertext, Long encryptKeyId) {
        if (StringUtils.isBlank(ciphertext)) {
            return null;
        }

        // TODO 应该维护多套密钥用于轮转。此处临时比对当前keyId，实际应该获取对应keyId的密钥解密
        if (!encryptKeyId.equals(keyId)) {
            throw new IllegalArgumentException(McUtils.i18n(RespMc.BAD_CIPHERTEXT));
        }

        try {
            return RSAUtils.decrypt(rsaPrivateKey, ciphertext);
        } catch (Exception e) {
            throw new IllegalArgumentException(McUtils.i18n(RespMc.BAD_CIPHERTEXT));
        }
    }
}
