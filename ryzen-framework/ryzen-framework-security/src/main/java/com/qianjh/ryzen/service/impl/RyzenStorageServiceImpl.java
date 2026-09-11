package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.SecurityProperties;
import com.qianjh.ryzen.service.RyzenStorageService;
import com.qianjh.ryzen.util.AESUtils;
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
        return AESUtils.encrypt(securityProperties.getStorage().getKey().getSecret(), plaintext);
    }

    @Override
    public String decrypt(String ciphertext) {
        return AESUtils.decrypt(securityProperties.getStorage().getKey().getSecret(), ciphertext);
    }
}
