package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.RsaKey;
import com.qianjh.ryzen.config.RsaProperties;
import com.qianjh.ryzen.service.RsaService;
import com.qianjh.ryzen.util.RSAUtils;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author QianJH
 */
public abstract class RsaServiceImpl implements RsaService {
    protected Map<Long, RSAPublicKey> publicKeys = new ConcurrentHashMap<>();
    protected Map<Long, RSAPrivateKey> privateKeys = new ConcurrentHashMap<>();

    @Override
    public RSAPublicKey getPublicKey(RsaProperties properties) {
        // 获取当前keyId
        Long keyId = properties.getCurrentKeyId();
        // 获取公钥
        RSAPublicKey publicKey = publicKeys.get(keyId);
        // 存在直接返回
        if (publicKey != null) {
            return publicKey;
        }

        // 从配置文件获取指定keyId公钥文本
        RsaKey rsaKey = properties.getKeys().get(keyId);
        // 如果公钥不存在，直接返回
        if (rsaKey == null) {
            return null;
        }

        // 构建公钥
        publicKey = RSAUtils.buildRsaPublicKey(rsaKey.getPublicKey());
        // 加入缓存
        publicKeys.put(keyId, publicKey);

        return publicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey(RsaProperties properties) {
        Long keyId = properties.getCurrentKeyId();

        RSAPrivateKey privateKey = privateKeys.get(keyId);
        if (privateKey != null) {
            return privateKey;
        }

        RsaKey rsaKey = properties.getKeys().get(keyId);
        if (rsaKey == null) {
            return null;
        }

        privateKey = RSAUtils.buildRsaPrivateKey(rsaKey.getPrivateKey());
        privateKeys.put(keyId, privateKey);

        return privateKey;
    }

    @Override
    public RSAPublicKey getPublicKey(Long keyId) {
        return publicKeys.get(keyId);
    }

    @Override
    public RSAPrivateKey getPrivateKey(Long keyId) {
        return privateKeys.get(keyId);
    }
}
