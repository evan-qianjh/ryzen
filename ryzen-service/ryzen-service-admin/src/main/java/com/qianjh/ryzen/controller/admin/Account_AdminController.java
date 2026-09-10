package com.qianjh.ryzen.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.api.RespMc;
import com.qianjh.ryzen.api.dto.OffsetPage;
import com.qianjh.ryzen.controller.admin.dto.*;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.AccountSecretService;
import com.qianjh.ryzen.service.AccountService;
import com.qianjh.ryzen.service.CreateAccountService;
import com.qianjh.ryzen.util.IdUtils;
import com.qianjh.ryzen.util.McUtils;
import com.qianjh.ryzen.util.PageUtils;
import com.qianjh.ryzen.util.PasswordUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.controller.admin._AdminController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Account_AdminController extends _AdminController {

    private final AccountService accountService;
    private final CreateAccountService createAccountService;
    private final AccountSecretService accountSecretService;

    @Operation(summary = "获取信息")
    @GetMapping("/account")
    public Resp<GetAccountResp> get(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                    @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                    @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId) {

        Account account = accountService.getById(oemId, tenantId, accountId);
        if (account == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        GetAccountResp result = GetAccountResp.builder()
                .id(IdUtils.toString(account.getId()))
                .nickname(account.getNickname())
                .username(account.getUsername())
                .administrator(account.isAdministrator())
                .build();
        return Resp.successOf(result);
    }

    @Operation(summary = "列表")
    @GetMapping("/accounts")
    public Resp<OffsetPage<GetAccountsResp>> page(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                                  @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                                  @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                                                  @RequestParam(required = false) String username,
                                                  @RequestParam(required = false) Boolean enabled,
                                                  @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                  @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Max(100) Integer pageSize) {
        Page<Account> page = accountService.page(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Account>()
                .eq(Account::getOemId, oemId)
                .eq(Account::getTenantId, tenantId)
                .eq(enabled != null, Account::getEnabled, enabled)
                .like(StringUtils.isNotBlank(username), Account::getUsername, username)
                .orderByDesc(Account::getId)
        );

        List<Account> _records = page.getRecords();

        List<GetAccountsResp> records = _records.stream()
                .map(e -> GetAccountsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .nickname(e.getNickname())
                        .username(e.getUsername())
                        .enabled(e.getEnabled())
                        .administrator(e.isAdministrator())
                        .build()
                ).toList();

        return Resp.successOf(PageUtils.wrap(page, records));
    }

    @Operation(summary = "创建")
    @PostMapping("/account")
    public Resp<PostAccountResp> post(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                      @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                      @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                                      @RequestBody @Validated PostAccountReq body) {
        Account entity = accountService.getByUsername(oemId, tenantId, body.getUsername());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }

        Pair<Account, String> pair = createAccountService.create(body, tenantId);
        entity = pair.getLeft();
        String password = pair.getRight();

        PostAccountResp result = PostAccountResp.builder()
                .nickname(entity.getNickname())
                .username(entity.getUsername())
                .password(password)
                .build();

        return Resp.successOf(result);
    }

    @Operation(summary = "修改")
    @PatchMapping("/account/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                         @PathVariable Long id,
                         @RequestBody @Validated PatchAccountReq body) {
        Account entity = accountService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        if (entity.isAdministrator()) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ACCESS));
        }
        boolean success = accountService.patch(oemId, tenantId, id, body);

        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "重制密码")
    @PutMapping("/account/{id}/password")
    public Resp<PutAccountPasswordResp> post(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                             @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                             @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                                             @PathVariable Long id) {
        Account entity = accountService.getById(oemId, tenantId, id);
        if (entity.isAdministrator()) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ACCESS));
        }

        String password = PasswordUtils.generate();
        accountSecretService.putLoginPassword(entity, password);

        PutAccountPasswordResp result = PutAccountPasswordResp.builder()
                .nickname(entity.getNickname())
                .username(entity.getUsername())
                .password(password)
                .build();

        return Resp.successOf(result);
    }
}
