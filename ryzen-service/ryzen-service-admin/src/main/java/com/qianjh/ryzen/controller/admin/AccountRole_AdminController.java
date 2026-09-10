package com.qianjh.ryzen.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.admin.dto.GetAccountRolesResp;
import com.qianjh.ryzen.controller.admin.dto.PostAccountRoleReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountRole;
import com.qianjh.ryzen.entity.Role;
import com.qianjh.ryzen.service.AccountRoleService;
import com.qianjh.ryzen.service.AccountService;
import com.qianjh.ryzen.service.RoleService;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.api.RespMc;
import com.qianjh.ryzen.api.dto.Receipt;
import com.qianjh.ryzen.controller.admin._AdminController;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
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
    public Resp<List<GetAccountRolesResp>> get(@RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                               @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long adminAccountId,
                                               Long accountId) {
        List<AccountRole> entities = accountRoleService.list(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getTenantId, tenantId)
                .eq(AccountRole::getAccountId, accountId)
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
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long adminAccountId,
                              @RequestBody @Validated PostAccountRoleReq body) {
        Account account = accountService.getById(body.getAccountId(), tenantId);
        Role role = roleService.getById(body.getRoleId(), tenantId);
        if (account == null || role == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }

        AccountRole entity = accountRoleService.createIfAbsent(account.getId(), role.getId(), tenantId);
        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/account-role/{id}")
    public Resp<?> del(@RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                       @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long adminAccountId,
                       @PathVariable Long id) {
        boolean success = accountRoleService.removeById(id, tenantId);

        return success ? Resp.success() : Resp.failure();
    }
}
