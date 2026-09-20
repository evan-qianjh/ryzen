package com.qianjh.ryzen.framework.security.service.impl;

import com.qianjh.ryzen.framework.security.config.RsaProperties;
import com.qianjh.ryzen.framework.security.config.SecurityProperties;
import com.qianjh.ryzen.framework.security.service.RyzenTokenService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RyzenTokenServiceImpl extends RsaServiceImpl implements RyzenTokenService {

    private final SecurityProperties securityProperties;

    @PostConstruct
    public void init() {
        RsaProperties properties = securityProperties.getToken();
        getPublicKey(properties);
        getPrivateKey(properties);
    }

}
