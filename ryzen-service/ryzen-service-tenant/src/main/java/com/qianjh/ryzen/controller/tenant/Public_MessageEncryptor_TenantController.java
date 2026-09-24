package com.qianjh.ryzen.controller.tenant;

import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.security.config.RsaProperties;
import com.qianjh.ryzen.framework.security.config.SecurityProperties;
import com.qianjh.ryzen.controller.tenant.dto.GetMessageEncryptorResp;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.security.service.RyzenMessageService;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.security.util.RSAUtils;
import com.qianjh.ryzen.framework.service.controller.tenant._TenantController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantController.PUBLIC_PATH_PREFIX;

@Slf4j
@Tag(name = "public-消息加密器")
@RestController
@RequestMapping(PUBLIC_PATH_PREFIX)
@RequiredArgsConstructor
public class Public_MessageEncryptor_TenantController extends _TenantController {

    private final RyzenMessageService ryzenPayloadCryptoService;
    private final SecurityProperties securityProperties;

    @Operation(summary = "获取")
    @GetMapping("/message-encryptor")
    public Resp<GetMessageEncryptorResp> get(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId) {
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
