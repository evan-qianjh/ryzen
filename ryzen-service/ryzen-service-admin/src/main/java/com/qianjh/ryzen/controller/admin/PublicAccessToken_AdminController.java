package com.qianjh.ryzen.controller.admin;


import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.controller.admin.dto.PostAccessTokenReq;
import com.qianjh.ryzen.controller.admin.dto.PostAccessTokenResp;
import com.qianjh.ryzen.exception.TokenExpiredException;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.AccountTokenService;
import com.qianjh.ryzen.service.HttpRequestService;
import com.qianjh.ryzen.service.RyzenTokenCryptoService;
import com.qianjh.ryzen.service.dto.AccessToken;
import com.qianjh.ryzen.service.dto.RefreshToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.qianjh.ryzen.controller.admin._AdminController.PUBLIC_PATH_PREFIX;

@Slf4j
@Tag(name = "public-AccessToken")
@RestController
@RequestMapping(PUBLIC_PATH_PREFIX)
@RequiredArgsConstructor
public class PublicAccessToken_AdminController extends _AdminController {

    private final HttpRequestService httpRequestService;
    private final AccountTokenService accountTokenService;
    private final RyzenTokenCryptoService ryzenTokenCryptoService;

    @Operation(summary = "创建", description = "根据refreshToken刷新accessToken")
    @PostMapping("/access-token")
    public Resp<PostAccessTokenResp> create(HttpServletRequest request,
                                            @RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                            @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                            @RequestBody @Validated PostAccessTokenReq body) {
        ClientInfo clientInfo = httpRequestService.getClientInfo(request);

        // 获取rsa
        RSAPublicKey rsaPublicKey = ryzenTokenCryptoService.getPublicKey();
        RefreshToken refreshToken = accountTokenService.parseRefreshToken(oemId, tenantId, body.getRefreshToken(), rsaPublicKey);
        if (refreshToken == null) {
            throw new TokenExpiredException();
        }

        // 获取私钥生成accessToken
        RSAPrivateKey rsaPrivateKey = ryzenTokenCryptoService.getPrivateKey();
        AccessToken accessToken = accountTokenService.generateAccessToken(oemId, tenantId, refreshToken, rsaPrivateKey, clientInfo);

        PostAccessTokenResp result = PostAccessTokenResp.builder()
                .accessToken(accessToken.getToken())
                .build();

        return Resp.successOf(result);
    }

}
