package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountToken;
import com.qianjh.ryzen.mapper.AccountTokenMapper;
import com.qianjh.ryzen.service.AccountTokenService;
import com.qianjh.ryzen.service.HttpRequestService;
import com.qianjh.ryzen.service.TokenService;
import com.qianjh.ryzen.service.dto.AccessToken;
import com.qianjh.ryzen.service.dto.RefreshToken;
import com.qianjh.ryzen.service.dto.TokenPayload;
import com.qianjh.ryzen.util.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountTokenServiceImpl extends ServiceImpl<AccountTokenMapper, AccountToken> implements AccountTokenService {
    private final TokenService tokenService;
    private final HttpRequestService httpRequestService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AccountToken create(Account account, RefreshToken refreshToken, ClientInfo clientInfo) {
        TokenPayload payload = refreshToken.getPayload();

        String clientInfoJson = httpRequestService.toJson(clientInfo);

        // 保存账户token(出于安全考虑，不能持久化jwt，避免泄露)
        AccountToken entity = AccountToken.builder()
                .tenantId(account.getTenantId())
                .accountId(account.getId())
                .clientInfo(clientInfoJson)
                .refreshTokenId(payload.getId())
                .generatedTime(DateTimeUtils.getLocalDateTime(payload.getGeneratedTime()))
                .expiredTime(DateTimeUtils.getLocalDateTime(payload.getExpiresTime()))
                .build();

        save(entity);

        return entity;
    }

    @Override
    public RefreshToken generateRefreshToken(Long tenantId, Account account, RSAPrivateKey privateKey, ClientInfo clientInfo) {
        return tokenService.generateRefreshToken(account.getId(), privateKey);
    }

    @Override
    public AccessToken generateAccessToken(Long oemId, Long tenantId, RefreshToken refreshToken, RSAPrivateKey privateKey, ClientInfo clientInfo) {
        return tokenService.generateAccessToken(refreshToken, privateKey);
    }

    @Override
    public RefreshToken parseRefreshToken(Long oemId, Long tenantId, String token, RSAPublicKey publicKey) {
        TokenPayload payload = tokenService.parseTokenPayload(token, publicKey);
        if (payload == null) {
            return null;
        }
        return RefreshToken.builder()
                .token(token)
                .payload(payload)
                .build();
    }
}
