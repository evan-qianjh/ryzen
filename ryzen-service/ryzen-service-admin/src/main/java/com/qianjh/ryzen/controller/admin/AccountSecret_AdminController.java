package com.qianjh.ryzen.controller.admin;

import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.controller.admin.dto.PutAccountSecretPasswordReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountSecret;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.AccountSecretService;
import com.qianjh.ryzen.service.AccountService;
import com.qianjh.ryzen.service.RyzenPayloadCryptoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.qianjh.ryzen.controller.admin._AdminController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户安全")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class AccountSecret_AdminController extends _AdminController {

    private final AccountService accountService;
    private final AccountSecretService accountSecretService;
    private final RyzenPayloadCryptoService ryzenPayloadCryptoService;


    @Operation(summary = "修改密码")
    @PutMapping("/account-secret/password")
    public Resp<?> post(@RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                        @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long adminAccountId,
                        @RequestBody @Validated PutAccountSecretPasswordReq body) {

        Account account = accountService.getById(adminAccountId, tenantId);

        AccountSecret secret = accountSecretService.getByAccount(account);

        // 传输解密
        String oldPassword = ryzenPayloadCryptoService.decrypt(body.getOldPassword());
        String newPassword = ryzenPayloadCryptoService.decrypt(body.getNewPassword());

        boolean success = accountSecretService.modifyLoginPassword(account, newPassword, oldPassword);

        return success ? Resp.success() : Resp.failure();
    }

}
