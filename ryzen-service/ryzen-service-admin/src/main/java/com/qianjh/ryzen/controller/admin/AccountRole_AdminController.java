package com.qianjh.ryzen.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.api.RespMc;
import com.qianjh.ryzen.api.dto.Receipt;
import com.qianjh.ryzen.controller.admin.dto.GetAccountRolesResp;
import com.qianjh.ryzen.controller.admin.dto.PostAccountRoleReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountRole;
import com.qianjh.ryzen.entity.Role;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.AccountRoleService;
import com.qianjh.ryzen.service.AccountService;
import com.qianjh.ryzen.service.RoleService;
import com.qianjh.ryzen.util.IdUtils;
import com.qianjh.ryzen.util.McUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.controller.admin._AdminController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户角色")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class AccountRole_AdminController extends _AdminController {

    private final AccountRoleService accountRoleService;
    private final AccountService accountService;
    private final RoleService roleService;

    @Operation(summary = "列表")
    @GetMapping("/account-roles")
    public Resp<List<GetAccountRolesResp>> get(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                               @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                               @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                                               Long targetAccountId) {
        List<AccountRole> entities = accountRoleService.list(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getOemId, oemId)
                .eq(AccountRole::getTenantId, tenantId)
                .eq(AccountRole::getAccountId, targetAccountId)
        );

        List<GetAccountRolesResp> result = entities.stream()
                .map(e -> GetAccountRolesResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .roleId(IdUtils.toString(e.getRoleId()))
                        .build())
                .toList();

        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/account-role")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                              @RequestBody @Validated PostAccountRoleReq body) {
        Account account = accountService.getById(oemId, tenantId, body.getAccountId());
        Role role = roleService.getById(oemId, tenantId, body.getRoleId());
        if (account == null || role == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }

        AccountRole entity = accountRoleService.createIfAbsent(oemId, tenantId, account.getId(), role.getId());
        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/account-role/{id}")
    public Resp<?> del(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                       @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                       @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                       @PathVariable Long id) {
        boolean success = accountRoleService.removeById(oemId, tenantId, id);

        return success ? Resp.success() : Resp.failure();
    }
}
