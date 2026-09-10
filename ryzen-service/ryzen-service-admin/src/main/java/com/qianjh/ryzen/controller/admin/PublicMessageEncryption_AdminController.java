package com.qianjh.ryzen.controller.admin;

import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.controller.admin.dto.GetMessageEncryptionResp;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.RyzenPayloadCryptoService;
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
@Tag(name = "public-消息加密")
@RestController
@RequestMapping(PUBLIC_PATH_PREFIX)
@RequiredArgsConstructor
public class PublicMessageEncryption_AdminController extends _AdminController {

    private final RyzenPayloadCryptoService ryzenPayloadCryptoService;

    @Operation(summary = "获取")
    @GetMapping("/message-encryption")
    public Resp<GetMessageEncryptionResp> get(@RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId) {

        RSAPublicKey publicKey = ryzenPayloadCryptoService.getPublicKey();
        Long keyId = ryzenPayloadCryptoService.getKeyId();

        GetMessageEncryptionResp result = GetMessageEncryptionResp.builder()
                .id(IdUtils.toString(keyId))
                .publicKey(RSAUtils.toString(publicKey))
                .build();

        return Resp.successOf(result);
    }
}
