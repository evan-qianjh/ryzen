package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.controller.tenant.app.dto.*;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.util.McUtils;
import com.qianjh.ryzen.framework.security.util.PasswordUtils;
import com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.service.util.PageUtils;
import com.qianjh.ryzen.service.AccountRoleService;
import com.qianjh.ryzen.service.AccountSecretService;
import com.qianjh.ryzen.service.AccountService;
import com.qianjh.ryzen.service.CreateAccountService;
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

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Account_TenantAppController extends _TenantAppController {

    private final AccountService accountService;
    private final CreateAccountService createAccountService;
    private final AccountSecretService accountSecretService;
    private final AccountRoleService accountRoleService;

    @Operation(summary = "获取信息")
    @GetMapping("/account")
    public Resp<GetAccountResp> get(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                    @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                    @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId) {

        Account account = accountService.getById(oemId, tenantId, accountId);
        if (account == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        GetAccountResp result = GetAccountResp.builder()
                .id(IdUtils.toString(account.getId()))
                .nickname(account.getNickname())
                .username(account.getUsername())
                .email(account.getEmail())
                .administrator(account.isAdministrator())
                .build();
        return Resp.successOf(result);
    }

    @Operation(summary = "分页查询")
    @GetMapping("/accounts")
    public Resp<OffsetPage<GetAccountsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                  @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                  @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                  //
                                                  @RequestParam(required = false) String username,
                                                  @RequestParam(required = false) Boolean enabled,
                                                  @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                  @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Max(100) Integer pageSize) {
        // TODO accountId鉴权

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
                        .email(e.getEmail())
                        .enabled(e.getEnabled())
                        .administrator(e.isAdministrator())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                ).toList();

        return Resp.successOf(PageUtils.wrap(page, records));
    }

    @Operation(summary = "创建")
    @PostMapping("/account")
    public Resp<PostAccountResp> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                      @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                      @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                      //
                                      @RequestBody @Validated PostAccountReq body) {
        // TODO accountId记录操作日志

        Account entity = accountService.getByUsername(oemId, body.getUsername());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }

        Pair<Account, String> pair = createAccountService.create(oemId, tenantId, body);
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
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         //
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

    @Operation(summary = "修改角色")
    @PutMapping("/account/{id}/roles")
    public Resp<?> putRoles(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                            @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                            @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                            //
                            @PathVariable Long id,
                            @RequestBody @Validated PutAccountRolesReq body) {
        accountRoleService.putRoles(oemId, tenantId, id, body.roleIds());
        return Resp.success();
    }

    @Operation(summary = "重制密码")
    @PutMapping("/account/{id}/password")
    public Resp<PutAccountPasswordResp> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                             @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                             @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                             //
                                             @PathVariable Long id) {
        Account entity = accountService.getById(oemId, tenantId, id);
        if (entity.isAdministrator()) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ACCESS));
        }

        String password = PasswordUtils.generate();
        accountSecretService.putLoginPassword(entity, password);

        // TODO 若已设置联系方式[email]，通过联系方式发送，未设置则直接返回给前端，告知用户主动修改密码

        PutAccountPasswordResp result = PutAccountPasswordResp.builder()
                .nickname(entity.getNickname())
                .username(entity.getUsername())
                .password(password)
                .build();

        return Resp.successOf(result);
    }
}
