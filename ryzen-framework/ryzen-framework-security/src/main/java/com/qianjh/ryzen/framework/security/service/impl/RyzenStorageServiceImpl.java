package com.qianjh.ryzen.framework.security.service.impl;

import com.qianjh.ryzen.framework.security.config.SecurityProperties;
import com.qianjh.ryzen.framework.security.service.RyzenStorageService;
import com.qianjh.ryzen.framework.security.util.AESUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RyzenStorageServiceImpl extends AesServiceImpl implements RyzenStorageService {

    private final SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        secret = securityProperties.getStorage().getKey().getSecret();
    }

    @Override
    public String encrypt(String plaintext) {
        return AESUtils.encrypt(secret, plaintext);
    }

    @Override
    public String decrypt(String ciphertext) {
        return AESUtils.decrypt(secret, ciphertext);
    }
}
