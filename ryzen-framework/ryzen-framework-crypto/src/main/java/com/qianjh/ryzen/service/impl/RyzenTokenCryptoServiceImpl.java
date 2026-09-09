package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.config.TokenProperties;
import com.qianjh.ryzen.service.RyzenTokenCryptoService;
import com.qianjh.ryzen.util.RSAUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RyzenTokenCryptoServiceImpl extends RsaCryptoServiceImpl implements RyzenTokenCryptoService {

    private final TokenProperties tokenProperties;

    @PostConstruct
    public void init() {
        keyId = tokenProperties.getKeyId();
        rsaPrivateKey = RSAUtils.buildRsaPrivateKey(tokenProperties.getRsaPrivateKey());
        rsaPublicKey = RSAUtils.buildRsaPublicKey(tokenProperties.getRsaPublicKey());
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return rsaPublicKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return rsaPrivateKey;
    }
}
