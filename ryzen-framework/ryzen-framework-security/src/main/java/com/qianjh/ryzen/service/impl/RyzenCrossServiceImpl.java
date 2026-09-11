
package com.qianjh.ryzen.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.qianjh.ryzen.config.RsaKey;
import com.qianjh.ryzen.config.RsaProperties;
import com.qianjh.ryzen.config.SecurityProperties;
import com.qianjh.ryzen.service.RyzenCrossService;
import com.qianjh.ryzen.service.dto.RsaEncrypt;
import com.qianjh.ryzen.util.GsonUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RyzenCrossServiceImpl extends RsaServiceImpl implements RyzenCrossService {

    private static final String DATA_KEY = "data";

    private final SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        RsaProperties properties = securityProperties.getCross();
        getPublicKey(properties);
        getPrivateKey(properties);
    }

    @Override
    public RsaEncrypt encrypt(String plainText) {
        return this.encrypt(plainText, Duration.ofDays(1));
    }

    @Override
    public RsaEncrypt encrypt(String plainText, Duration expire) {
        RsaProperties properties = securityProperties.getCross();
        Long keyId = properties.getCurrentKeyId();
        RSAPrivateKey privateKey = getPrivateKey(properties);

        // 生成
        Date expiresAt = Date.from(LocalDateTime.now()
                .plusSeconds(expire.getSeconds())
                .atZone(ZoneId.systemDefault()).toInstant()
        );
        Algorithm algorithm = Algorithm.RSA256(privateKey);
        //
        String sign = JWT.create()
                .withExpiresAt(expiresAt)
                .withClaim(DATA_KEY, plainText)
                .sign(algorithm);
        return new RsaEncrypt(keyId, sign);
    }

    @Override
    public RsaEncrypt encryptObj(Object obj) {
        String json = GsonUtils.toJson(obj);
        return encrypt(json);
    }

    @Override
    public String decrypt(Long keyId, String cipherText) {
        if (StringUtils.isBlank(cipherText)) {
            return null;
        }

        // expired
        RsaProperties properties = securityProperties.getCross();
        RsaKey rsaKey = properties.getKeys().get(keyId);
        Long expireTime = rsaKey.getExpireTime();
        if (expireTime != null && expireTime < System.currentTimeMillis()) {
            log.warn("访问已过期的RsaKey ::: {}", keyId);
            return null;
        }

        // 获取公钥
        RSAPublicKey publicKey = getPublicKey(keyId);

        Algorithm algorithm = Algorithm.RSA256(publicKey);
        //
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();

        // TODO 增加时间校验

        DecodedJWT jwt;
        try {
            jwt = jwtVerifier.verify(cipherText);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        if (jwt == null) {
            return null;
        }
        return jwt.getClaim(DATA_KEY).asString();
    }

    @Override
    public <T> T decrypt(Long keyId, String cipherText, Class<T> clazz) {
        String decrypt = decrypt(keyId, cipherText);
        if (StringUtils.isBlank(decrypt)) {
            return null;
        }
        return GsonUtils.GSON.fromJson(decrypt, clazz);
    }
}
