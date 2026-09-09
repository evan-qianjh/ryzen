
package com.qianjh.ryzen.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.qianjh.ryzen.config.CrossProperties;
import com.qianjh.ryzen.service.RyzenCrossCryptoService;
import com.qianjh.ryzen.util.RSAUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
public class RyzenCrossCryptoServiceImpl extends RsaCryptoServiceImpl implements RyzenCrossCryptoService {

    private final CrossProperties crossProperties;

    @PostConstruct
    public void init() {
        keyId = crossProperties.getKeyId();
        rsaPrivateKey = RSAUtils.buildRsaPrivateKey(crossProperties.getRsaPrivateKey());
        rsaPublicKey = RSAUtils.buildRsaPublicKey(crossProperties.getRsaPublicKey());
    }

    private static final String DATA_KEY = "data";

    private final Gson GSON = new Gson();

    @Override
    public String encrypt(String plainText) {
        return this.encrypt(plainText, Duration.ofDays(1));
    }

    @Override
    public String encrypt(String plainText, Duration expire) {
        // 生成
        Date expiresAt = Date.from(LocalDateTime.now()
                .plusSeconds(expire.getSeconds())
                .atZone(ZoneId.systemDefault()).toInstant()
        );
        Algorithm algorithm = Algorithm.RSA256(rsaPrivateKey);
        //
        return JWT.create()
                .withExpiresAt(expiresAt)
                .withClaim(DATA_KEY, plainText)
                .sign(algorithm);
    }

    @Override
    public String encryptObj(Object obj) {
        String json = GSON.toJson(obj);
        return encrypt(json);
    }

    @Override
    public String decrypt(String cipherText) {
        if (StringUtils.isBlank(cipherText)) {
            return null;
        }
        Algorithm algorithm = Algorithm.RSA256(rsaPublicKey);
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
    public <T> T decrypt(String cipherText, Class<T> clazz) {
        String decrypt = decrypt(cipherText);
        if (StringUtils.isBlank(decrypt)) {
            return null;
        }
        return GSON.fromJson(decrypt, clazz);
    }
}
