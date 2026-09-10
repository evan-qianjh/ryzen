package com.qianjh.ryzen.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.api.Resp;
import com.qianjh.ryzen.api.RespMc;
import com.qianjh.ryzen.api.dto.Receipt;
import com.qianjh.ryzen.controller.admin.dto.GetRolePermissionsResp;
import com.qianjh.ryzen.controller.admin.dto.PostRolePermissionReq;
import com.qianjh.ryzen.entity.Permission;
import com.qianjh.ryzen.entity.Role;
import com.qianjh.ryzen.entity.RolePermission;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.PermissionService;
import com.qianjh.ryzen.service.RolePermissionService;
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
@Tag(name = "角色权限")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class RolePermission_AdminController extends _AdminController {

    private final RolePermissionService rolePermissionService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @Operation(summary = "列表")
    @GetMapping("/role-permissions")
    public Resp<List<GetRolePermissionsResp>> get(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                                  @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                                  @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                                                  Long roleId) {
        List<RolePermission> entities = rolePermissionService.list(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getOemId, oemId)
                .eq(RolePermission::getTenantId, tenantId)
                .eq(RolePermission::getRoleId, roleId)
        );

        List<GetRolePermissionsResp> result = entities.stream()
                .map(e -> GetRolePermissionsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .permissionId(IdUtils.toString(e.getPermissionId()))
                        .build())
                .toList();

        return Resp.successOf(result);

    }

    @Operation(summary = "创建")
    @PostMapping("/role-permission")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                              @RequestBody @Validated PostRolePermissionReq body) {
        Role role = roleService.getById(oemId, tenantId, body.getRoleId());
        Permission permission = permissionService.getById(oemId, tenantId, body.getPermissionId());
        if (role == null || permission == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }

        RolePermission entity = rolePermissionService.createIfAbsent(oemId, tenantId, role.getId(), permission.getId());
        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/role-permission/{id}")
    public Resp<?> del(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                       @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                       @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                       @PathVariable Long id) {
        boolean success = rolePermissionService.removeById(oemId, tenantId, id);

        return success ? Resp.success() : Resp.failure();
    }
}
