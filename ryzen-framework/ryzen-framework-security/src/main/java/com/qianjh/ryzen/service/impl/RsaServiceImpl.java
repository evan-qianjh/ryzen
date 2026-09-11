package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.RsaKey;
import com.qianjh.ryzen.config.RsaProperties;
import com.qianjh.ryzen.service.RsaService;
import com.qianjh.ryzen.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author QianJH
 */
@Slf4j
public abstract class RsaServiceImpl implements RsaService {
    private final Map<Long, RSAPublicKey> publicKeys = new ConcurrentHashMap<>();
    private final Map<Long, RSAPrivateKey> privateKeys = new ConcurrentHashMap<>();

    @Override
    public RSAPublicKey getPublicKey(RsaProperties properties) {
        Long keyId = properties.getCurrentKeyId();

        // cache get
        RSAPublicKey publicKey = publicKeys.get(keyId);
        // 存在直接返回
        if (publicKey != null) {
            return publicKey;
        }

        // properties get
        RsaKey rsaKey = properties.getKeys().get(keyId);
        if (rsaKey == null) {
            log.error("未配置RsaKey : getPublicKey ::: keyId={}", keyId);
            return null;
        }
        // expired
        Long expireTime = rsaKey.getExpireTime();
        if (expireTime != null && expireTime < System.currentTimeMillis()) {
            log.warn("访问已过期的rsaPublicKey ::: keyId={}", keyId);
            return null;
        }

        // build & cache
        publicKey = RSAUtils.buildRsaPublicKey(rsaKey.getPublicKey());
        publicKeys.put(keyId, publicKey);

        return publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey(RsaProperties properties) {
        Long keyId = properties.getCurrentKeyId();

        // cache get
        RSAPrivateKey privateKey = privateKeys.get(keyId);
        if (privateKey != null) {
            return privateKey;
        }

        // properties get
        RsaKey rsaKey = properties.getKeys().get(keyId);
        if (rsaKey == null) {
            log.error("未配置RsaKey : getPrivateKey ::: keyId={}", keyId);
            return null;
        }
        // expired
        Long expireTime = rsaKey.getExpireTime();
        if (expireTime != null && expireTime < System.currentTimeMillis()) {
            log.warn("访问已过期的rsaPrivateKey ::: keyId={}", keyId);
            return null;
        }

        // build & cache
        privateKey = RSAUtils.buildRsaPrivateKey(rsaKey.getPrivateKey());
        privateKeys.put(keyId, privateKey);

        return privateKey;
    }

    @Override
    public RSAPublicKey getPublicKey(Long keyId) {
        // TODO 应该判断过期
        return publicKeys.get(keyId);
    }

    @Override
    public RSAPrivateKey getPrivateKey(Long keyId) {
        // TODO 应该判断过期
        return privateKeys.get(keyId);
    }
}
