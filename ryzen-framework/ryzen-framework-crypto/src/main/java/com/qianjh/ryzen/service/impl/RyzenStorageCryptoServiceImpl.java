package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.StorageProperties;
import com.qianjh.ryzen.service.RyzenStorageCryptoService;
import com.qianjh.ryzen.util.AESUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RyzenStorageCryptoServiceImpl extends AesCryptoServiceImpl implements RyzenStorageCryptoService {

    private final StorageProperties storageProperties;

    @PostConstruct
    public void init() {
        secret = storageProperties.getAesSecret();
    }

    @Override
    public String encrypt(String plaintext) {
        return AESUtils.encrypt(storageProperties.getAesSecret(), plaintext);
    }

    @Override
    public String decrypt(String ciphertext) {
        return AESUtils.decrypt(storageProperties.getAesSecret(), ciphertext);
    }
}
