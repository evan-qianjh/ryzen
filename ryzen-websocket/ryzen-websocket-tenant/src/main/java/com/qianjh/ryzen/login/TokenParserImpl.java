package com.qianjh.ryzen.login;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.security.config.RsaProperties;
import com.qianjh.ryzen.framework.security.config.SecurityProperties;
import com.qianjh.ryzen.framework.security.service.RyzenTokenService;
import com.qianjh.ryzen.framework.websocket.config.JwtConfig;
import com.qianjh.ryzen.framework.websocket.login.TokenParsed;
import com.qianjh.ryzen.framework.websocket.login.TokenParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPublicKey;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenParserImpl implements TokenParser {

    private final JwtConfig jwtConfig;
    private final RyzenTokenService ryzenTokenService;
    private final SecurityProperties securityProperties;

    @Override
    public TokenParsed parse(String token) {
        // 截掉前缀
        String prefix = jwtConfig.getPrefix();
        if (StringUtils.isNotBlank(prefix)) {
            token = token.replace(prefix, "");
        }

        RsaProperties properties = securityProperties.getToken();
        RSAPublicKey publicKey = ryzenTokenService.getPublicKey(properties);

        try {
            SignedJWT signedJwt = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);
            if (!signedJwt.verify(verifier)) {
                return null;
            }
            JWTClaimsSet claims = signedJwt.getJWTClaimsSet();

            String accountId = claims.getStringClaim("accountId");

            return TokenParsed.builder()
                    .accountId(IdUtils.ofString(accountId))
                    .extraHeaders(Collections.emptyMap())
                    .build();
        } catch (Exception e) {
            return null;
        }

    }
}
