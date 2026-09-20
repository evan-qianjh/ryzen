package com.qianjh.ryzen.controller.admin;


import com.qianjh.ryzen.framework.common.dto.ClientInfo;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.security.config.RsaProperties;
import com.qianjh.ryzen.framework.security.config.SecurityProperties;
import com.qianjh.ryzen.controller.admin.dto.PostAccessTokenReq;
import com.qianjh.ryzen.controller.admin.dto.PostAccessTokenResp;
import com.qianjh.ryzen.framework.common.exception.TokenExpiredException;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.framework.service.controller.admin._AdminController;
import com.qianjh.ryzen.service.AccountTokenService;
import com.qianjh.ryzen.framework.servlet.service.HttpRequestService;
import com.qianjh.ryzen.framework.security.service.RyzenTokenService;
import com.qianjh.ryzen.framework.token.service.dto.AccessToken;
import com.qianjh.ryzen.framework.token.service.dto.RefreshToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.qianjh.ryzen.framework.service.controller.admin._AdminController.PUBLIC_PATH_PREFIX;

@Slf4j
@Tag(name = "public-AccessToken")
@RestController
@RequestMapping(PUBLIC_PATH_PREFIX)
@RequiredArgsConstructor
public class Public_AccessToken_AdminController extends _AdminController {

    private final HttpRequestService httpRequestService;
    private final AccountTokenService accountTokenService;
    private final RyzenTokenService ryzenTokenCryptoService;
    private final SecurityProperties securityProperties;

    @Operation(summary = "创建", description = "根据refreshToken刷新accessToken")
    @PostMapping("/access-token")
    public Resp<PostAccessTokenResp> create(HttpServletRequest request,
                                            @RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                            @RequestBody @Validated PostAccessTokenReq body) {
        ClientInfo clientInfo = httpRequestService.getClientInfo(request);

        RsaProperties properties = securityProperties.getToken();

        // 获取rsa
        RSAPublicKey rsaPublicKey = ryzenTokenCryptoService.getPublicKey(properties);
        RefreshToken refreshToken = accountTokenService.parseRefreshToken(oemId, body.getRefreshToken(), rsaPublicKey);
        if (refreshToken == null) {
            throw new TokenExpiredException();
        }

        // 获取私钥生成accessToken
        RSAPrivateKey rsaPrivateKey = ryzenTokenCryptoService.getPrivateKey(properties);
        AccessToken accessToken = accountTokenService.generateAccessToken(refreshToken, rsaPrivateKey, clientInfo);

        PostAccessTokenResp result = PostAccessTokenResp.builder()
                .accessToken(accessToken.getToken())
                .build();

        return Resp.successOf(result);
    }

}
