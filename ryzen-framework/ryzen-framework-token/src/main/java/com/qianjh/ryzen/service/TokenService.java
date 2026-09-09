package com.qianjh.ryzen.service;


import com.qianjh.ryzen.service.dto.AccessToken;
import com.qianjh.ryzen.service.dto.RefreshToken;
import com.qianjh.ryzen.service.dto.TokenPayload;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

/**
 * @author QianJH
 */
public interface TokenService {

    /**
     * 创建刷新token
     *
     * @param accountId  账户ID
     * @param duration   有效时长
     * @param privateKey 私钥
     * @return 刷新token
     */
    RefreshToken generateRefreshToken(Long accountId, Duration duration, RSAPrivateKey privateKey);

    /**
     * 创建刷新token
     *
     * @param accountId  账户ID
     * @param privateKey 私钥
     * @return 刷新token
     */
    RefreshToken generateRefreshToken(Long accountId, RSAPrivateKey privateKey);

    /**
     * 创建访问token
     *
     * @param refreshToken 刷新token
     * @param duration     有效时长
     * @param privateKey   私钥
     * @return 访问token
     */
    AccessToken generateAccessToken(RefreshToken refreshToken, Duration duration, RSAPrivateKey privateKey);

    /**
     * 创建访问token
     *
     * @param refreshToken 刷新token
     * @param privateKey   私钥
     * @return 访问token
     */
    AccessToken generateAccessToken(RefreshToken refreshToken, RSAPrivateKey privateKey);

    /**
     * 解析token
     *
     * @param token     Token
     * @param publicKey 公钥
     * @return token结构
     */
    TokenPayload parseTokenPayload(String token, RSAPublicKey publicKey);
}
