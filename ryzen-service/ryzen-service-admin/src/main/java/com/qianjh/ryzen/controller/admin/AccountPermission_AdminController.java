package com.qianjh.ryzen.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.admin.dto.GetAccountPermissionsResp;
import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.service.*;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.controller.admin._AdminController;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.util.IdUtils;
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

import static com.qianjh.ryzen.controller.admin._AdminController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户权限")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class AccountPermission_AdminController extends _AdminController {

    private final RoleService roleService;
    private final AccountRoleService accountRoleService;
    private final RolePermissionService rolePermissionService;
    private final PermissionService permissionService;
    private final AccountService accountService;

    @Operation(summary = "列表")
    @GetMapping("/account-permissions")
    public Resp<GetAccountPermissionsResp> get(@RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                               @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long adminAccountId) {

        GetAccountPermissionsResp result = GetAccountPermissionsResp.builder()
                .roles(new ArrayList<>())
                .permissions(new ArrayList<>())
                .build();

        // 超管
        Account account = accountService.getById(adminAccountId, tenantId);
        if (account.isAdministrator()) {
            List<Permission> permissions = permissionService.list(new LambdaQueryWrapper<Permission>()
                    .eq(Permission::getTenantId, tenantId)
            );
            result.getPermissions().addAll(
                    permissions.stream().map(this::map).toList()
            );
            return Resp.successOf(result);
        }


        // AccountRole
        List<AccountRole> accountRoles = accountRoleService.list(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getTenantId, tenantId)
                .eq(AccountRole::getAccountId, adminAccountId)
        );
        if (accountRoles.isEmpty()) {
            return Resp.successOf(result);
        }

        // roles
        List<Long> roleIds = accountRoles.stream().map(AccountRole::getRoleId).distinct().toList();
        List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>()
                .in(Role::getId, roleIds)
                .eq(Role::getTenantId, tenantId)
                .eq(Role::getEnabled, true)
        );
        if (roles.isEmpty()) {
            return Resp.successOf(result);
        }
        result.getRoles().addAll(
                roles.stream()
                        .map(e -> new GetAccountPermissionsResp.Role(
                                        IdUtils.toString(e.getId()),
                                        e.getTitle()
                                )
                        )
                        .toList()
        );

        // RolePermission
        List<RolePermission> rolePermissions = rolePermissionService.list(new LambdaQueryWrapper<RolePermission>()
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
        result.getPermissions().addAll(
                permissions.stream().map(this::map).toList()
        );

        return Resp.successOf(result);
    }

    private GetAccountPermissionsResp.Permission map(Permission e) {
        return new GetAccountPermissionsResp.Permission(
                IdUtils.toString(e.getId()),
                e.getTitle(),
                e.getSymbol()
        );
    }
}
