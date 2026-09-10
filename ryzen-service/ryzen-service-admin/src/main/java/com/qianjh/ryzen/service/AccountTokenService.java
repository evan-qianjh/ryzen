package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountToken;
import com.qianjh.ryzen.service.dto.AccessToken;
import com.qianjh.ryzen.service.dto.RefreshToken;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 *
 * @author QianJH
 */
public interface AccountTokenService extends IService<AccountToken> {

    /**
     * 创建
     *
     * @param account      账户
     * @param refreshToken 刷新Token
     * @param clientInfo   客户端信息
     * @return entity
     */
    AccountToken create(Account account, RefreshToken refreshToken, ClientInfo clientInfo);

    /**
     * 创建刷新token
     *
     * @param tenantId   租户ID
     * @param account    账户
     * @param privateKey 私钥
     * @param clientInfo 客户端信息
     * @return 账户token
     */
    RefreshToken generateRefreshToken(Long tenantId, Account account, RSAPrivateKey privateKey, ClientInfo clientInfo);

    /**
     * 创建访问token
     *
     * @param oemId        OEM ID
     * @param tenantId     租户ID
     * @param refreshToken 刷新token
     * @param privateKey   私钥
     * @param clientInfo   客户端信息
     * @return 账户token
     */
    AccessToken generateAccessToken(Long oemId, Long tenantId, RefreshToken refreshToken, RSAPrivateKey privateKey, ClientInfo clientInfo);

    /**
     * 解析token
     *
     * @param oemId     OEM ID
     * @param tenantId  租户ID
     * @param token     token
     * @param publicKey 公钥
     * @return payload
     */
    RefreshToken parseRefreshToken(Long oemId, Long tenantId, String token, RSAPublicKey publicKey);
}
