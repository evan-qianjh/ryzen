package com.qianjh.ryzen.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.qianjh.ryzen.config.JwtConfig;
import com.qianjh.ryzen.exception.TokenExpiredException;
import com.qianjh.ryzen.service.TokenService;
import com.qianjh.ryzen.service.dto.AccessToken;
import com.qianjh.ryzen.service.dto.RefreshToken;
import com.qianjh.ryzen.service.dto.TokenPayload;
import com.qianjh.ryzen.util.GsonUtils;
import com.qianjh.ryzen.util.LongUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final static String SUBSTRING_PREFIX = "Bearer ";
    private final JwtConfig jwtConfig;

    @Override
    public RefreshToken generateRefreshToken(Long oemId, Long tenantId, Long accountId, Duration duration, RSAPrivateKey privateKey) {
        // 构建token
        long generatedTime = System.currentTimeMillis();
        long expiresTime = generatedTime + duration.toMillis();

        // 构建token结构
        TokenPayload payload = TokenPayload.builder()
                .id(UUID.randomUUID().toString())
                .oemId(LongUtils.toString(oemId))
                .tenantId(LongUtils.toString(tenantId))
                .accountId(LongUtils.toString(accountId))
                .generatedTime(generatedTime)
                .expiresTime(expiresTime)
                .build();

        Algorithm algorithm = Algorithm.RSA256(privateKey);

        String token = JWT.create()
                .withExpiresAt(new Date(expiresTime))
                .withPayload(GsonUtils.toJson(payload))
                .sign(algorithm);

        return RefreshToken.builder()
                .token(token)
                .payload(payload)
                .build();
    }

    @Override
    public RefreshToken generateRefreshToken(Long oemId, Long tenantId, Long accountId, RSAPrivateKey privateKey) {
        Duration duration = Duration.ofDays(jwtConfig.getRefreshTokenDurationOfDays());
        return generateRefreshToken(oemId, tenantId, accountId, duration, privateKey);
    }

    @Override
    public AccessToken generateAccessToken(RefreshToken refreshToken, Duration duration, RSAPrivateKey privateKey) {
        Assert.notNull(refreshToken, IllegalArgumentException.class.getSimpleName());

        TokenPayload tokenPayload = refreshToken.getPayload();
        if (tokenPayload.getExpiresTime() < System.currentTimeMillis()) {
            throw new TokenExpiredException();
        }

        long generatedTime = System.currentTimeMillis();
        long expiresTime = generatedTime + duration.toMillis();

        TokenPayload payload = TokenPayload.builder()
                .id(UUID.randomUUID().toString())
                .oemId(tokenPayload.getOemId())
                .tenantId(tokenPayload.getTenantId())
                .accountId(tokenPayload.getAccountId())
                .generatedTime(generatedTime)
                .expiresTime(expiresTime)
                .build();

        // 获取私钥
        Algorithm algorithm = Algorithm.RSA256(privateKey);

        String token = JWT.create()
                .withExpiresAt(new Date(expiresTime))
                .withPayload(GsonUtils.toJson(payload))
                .sign(algorithm);

        return AccessToken.builder()
                .token(token)
                .payload(payload)
                .build();
    }

    @Override
    public AccessToken generateAccessToken(RefreshToken refreshToken, RSAPrivateKey privateKey) {
        Duration duration = Duration.ofMinutes(jwtConfig.getAccessTokenDurationOfMinutes());
        return generateAccessToken(refreshToken, duration, privateKey);
    }

    @Override
    public TokenPayload parseTokenPayload(String token, RSAPublicKey publicKey) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        if (token.startsWith(SUBSTRING_PREFIX)) {
            token = token.substring(SUBSTRING_PREFIX.length());
        }

        Algorithm algorithm = Algorithm.RSA256(publicKey);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        // 验证
        DecodedJWT jwt;
        try {
            jwt = jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            log.debug("jwt验证失败 ::: e={}", e.getMessage());
            return null;
        }

        // convert
        TokenPayload payload = new TokenPayload();
        Field[] fields = TokenPayload.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Claim claim = jwt.getClaim(field.getName());
            if (!claim.isNull()) {
                try {
                    Object value = getClaimValue(claim, field.getType());
                    field.set(payload, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return payload;
    }

    private static Object getClaimValue(Claim claim, Class<?> type) {
        if (type == String.class) {
            return claim.asString();
        } else if (type == Integer.class || type == int.class) {
            return claim.asInt();
        } else if (type == Long.class || type == long.class) {
            return claim.asLong();
        } else if (type == Boolean.class || type == boolean.class) {
            return claim.asBoolean();
        } else if (type == Double.class || type == double.class) {
            return claim.asDouble();
        } else if (type == Date.class) {
            return claim.asDate();
        } else if (type == List.class) {
            return claim.asList(Object.class);
        } else if (type == Map.class) {
            return claim.asMap();
        }
        return null;
    }
}
