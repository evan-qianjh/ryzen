package com.qianjh.ryzen.controller.admin;

import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.config.RsaProperties;
import com.qianjh.ryzen.config.SecurityProperties;
import com.qianjh.ryzen.controller.admin.dto.GetMessageEncryptorResp;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.RyzenMessageService;
import com.qianjh.ryzen.util.IdUtils;
import com.qianjh.ryzen.util.RSAUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;

import static com.qianjh.ryzen.controller.admin._AdminController.PUBLIC_PATH_PREFIX;

@Slf4j
@Tag(name = "public-消息加密器")
@RestController
@RequestMapping(PUBLIC_PATH_PREFIX)
@RequiredArgsConstructor
public class Public_MessageEncryptor_AdminController extends _AdminController {

    private final RyzenMessageService ryzenPayloadCryptoService;
    private final SecurityProperties securityProperties;

    @Operation(summary = "获取")
    @GetMapping("/message-encryptor")
    public Resp<GetMessageEncryptorResp> get(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId) {
        RsaProperties properties = securityProperties.getMessage();
        RSAPublicKey publicKey = ryzenPayloadCryptoService.getPublicKey(properties);
        Long keyId = properties.getCurrentKeyId();

        GetMessageEncryptorResp result = GetMessageEncryptorResp.builder()
                .keyId(IdUtils.toString(keyId))
                .publicKey(RSAUtils.toString(publicKey))
                .build();

        return Resp.successOf(result);
    }
}
