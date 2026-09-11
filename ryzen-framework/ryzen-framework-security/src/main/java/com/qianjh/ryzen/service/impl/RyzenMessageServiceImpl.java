package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.api.RespMc;
import com.qianjh.ryzen.config.RsaProperties;
import com.qianjh.ryzen.config.SecurityProperties;
import com.qianjh.ryzen.service.RyzenMessageService;
import com.qianjh.ryzen.service.dto.RsaEncrypt;
import com.qianjh.ryzen.util.McUtils;
import com.qianjh.ryzen.util.RSAUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RyzenMessageServiceImpl extends RsaServiceImpl implements RyzenMessageService {

    private final SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        RsaProperties properties = securityProperties.getMessage();
        getPublicKey(properties);
        getPrivateKey(properties);
    }

    @Override
    public RsaEncrypt encrypt(String plaintext) {
        if (StringUtils.isBlank(plaintext)) {
            return null;
        }
        RsaProperties properties = securityProperties.getMessage();
        Long keyId = properties.getCurrentKeyId();

        RSAPublicKey publicKey = getPublicKey(properties);

        String encrypt = RSAUtils.encrypt(publicKey, plaintext);

        return new RsaEncrypt(keyId, encrypt);
    }

    @Override
    public String decrypt(Long keyId, String ciphertext) {
        if (StringUtils.isBlank(ciphertext)) {
            return null;
        }

        RSAPrivateKey privateKey = getPrivateKey(keyId);
        if (privateKey == null) {
            throw new IllegalArgumentException(McUtils.i18n(RespMc.BAD_CIPHERTEXT));
        }

        try {
            return RSAUtils.decrypt(privateKey, ciphertext);
        } catch (Exception e) {
            throw new IllegalArgumentException(McUtils.i18n(RespMc.BAD_CIPHERTEXT));
        }
    }
}
