package com.qianjh.ryzen.controller.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.dto.GetAccountPermissionsResp;
import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.service.controller.tenant._TenantController;
import com.qianjh.ryzen.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户权限")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class AccountPermission_TenantController extends _TenantController {

    private final RoleService roleService;
    private final AccountRoleService accountRoleService;
    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;
    private final AccountService accountService;

    @Operation(summary = "列表")
    @GetMapping("/account-permissions")
    public Resp<List<GetAccountPermissionsResp>> get(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                               @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                               @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId) {

        List<GetAccountPermissionsResp> result = new ArrayList<>();

        // 超管
        Account account = accountService.getById(oemId, tenantId, accountId);
        if (account.isAdministrator()) {
            List<Permission> permissions = permissionService.list(new LambdaQueryWrapper<Permission>()
                    .eq(Permission::getOemId, oemId)
                    .eq(Permission::getTenantId, tenantId)
            );
            result.addAll(
                    permissions.stream().map(this::map).toList()
            );
            return Resp.successOf(result);
        }


        // AccountRole
        List<AccountRole> accountRoles = accountRoleService.list(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getOemId, oemId)
                .eq(AccountRole::getTenantId, tenantId)
                .eq(AccountRole::getAccountId, accountId)
        );
        if (accountRoles.isEmpty()) {
            return Resp.successOf(result);
        }

        // roles
        List<Long> roleIds = accountRoles.stream().map(AccountRole::getRoleId).distinct().toList();
        List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>()
                .eq(Role::getOemId, oemId)
                .eq(Role::getTenantId, tenantId)
                .in(Role::getId, roleIds)
                .eq(Role::getEnabled, true)
        );
        if (roles.isEmpty()) {
            return Resp.successOf(result);
        }

        // RolePermission
        List<RolePermission> rolePermissions = rolePermissionService.list(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getOemId, oemId)
                .eq(RolePermission::getTenantId, tenantId)
                .in(RolePermission::getRoleId, roleIds)
        );
        if (rolePermissions.isEmpty()) {
            return Resp.successOf(result);
        }

        // permissions
        List<Long> permissionIds = rolePermissions.stream().map(RolePermission::getPermissionId).distinct().toList();
        List<Permission> permissions = permissionService.list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getTenantId, tenantId)
                .eq(Permission::getEnabled, true)
                .in(Permission::getId, permissionIds)
        );
        result.addAll(
                permissions.stream().map(this::map).toList()
        );

        return Resp.successOf(result);
    }

    private GetAccountPermissionsResp map(Permission e) {
        return new GetAccountPermissionsResp(
                IdUtils.toString(e.getId()),
                e.getTitle(),
                e.getSymbol()
        );
    }
}
