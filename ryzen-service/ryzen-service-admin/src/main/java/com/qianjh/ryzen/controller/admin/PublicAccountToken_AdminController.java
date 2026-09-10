package com.qianjh.ryzen.controller.admin;


import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.controller.admin.dto.PostAccountTokenByPasswordReq;
import com.qianjh.ryzen.controller.admin.dto.PostAccountTokenResp;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountToken;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.*;
import com.qianjh.ryzen.service.dto.AccessToken;
import com.qianjh.ryzen.service.dto.RefreshToken;
import com.qianjh.ryzen.util.IdUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.interfaces.RSAPrivateKey;
import java.util.Objects;

import static com.qianjh.ryzen.controller.admin._AdminController.PUBLIC_PATH_PREFIX;

@Slf4j
@Tag(name = "public-账户Token")
@RestController
@RequestMapping(PUBLIC_PATH_PREFIX)
@RequiredArgsConstructor
public class PublicAccountToken_AdminController extends _AdminController {

    private final HttpRequestService httpRequestService;
    private final AccountService accountService;
    private final AccountTokenService accountTokenService;
    private final RyzenTokenCryptoService zenTokenCryptoService;
    private final RyzenPayloadCryptoService ryzenPayloadCryptoService;


    @Operation(summary = "账号密码登录", description = "密码先通过RSA2048加密，然后提交")
    @PostMapping("/account-token")
    public Resp<PostAccountTokenResp> create(HttpServletRequest request,
                                             @RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                             @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                             @RequestBody @Validated PostAccountTokenByPasswordReq body) {

        ClientInfo clientInfo = httpRequestService.getClientInfo(request);

        // 传输解密
        String password = ryzenPayloadCryptoService.decrypt(body.getPassword());

        // 登录
        Account account = accountService.passwordLogin(oemId, tenantId, clientInfo, body.getUsername(), password, body.getTotp());
        if (Objects.isNull(account)) {
            return Resp.failure("Account or password incorrect");
        }

        RSAPrivateKey accountTokenPrivateKey = zenTokenCryptoService.getPrivateKey();
        // 生成refreshToken
        RefreshToken refreshToken = accountTokenService.generateRefreshToken(tenantId, account, accountTokenPrivateKey, clientInfo);
        // 生成accessToken
        AccessToken accessToken = accountTokenService.generateAccessToken(oemId, tenantId, refreshToken, accountTokenPrivateKey, clientInfo);

        // 创建账户token
        AccountToken accountToken = accountTokenService.create(account, refreshToken, clientInfo);

        PostAccountTokenResp dto = PostAccountTokenResp.builder()
                .id(IdUtils.toString(accountToken.getId()))
                .accessToken(accessToken.getToken())
                .refreshToken(refreshToken.getToken())
                .build();
        return Resp.successOf(dto);
    }

}
