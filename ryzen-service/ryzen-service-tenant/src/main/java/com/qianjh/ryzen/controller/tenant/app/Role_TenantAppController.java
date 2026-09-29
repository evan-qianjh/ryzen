package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetRolesResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PatchRoleReq;
import com.qianjh.ryzen.controller.tenant.app.dto.PostRoleReq;
import com.qianjh.ryzen.tenant.entity.Role;
import com.qianjh.ryzen.tenant.entity.RolePermission;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.util.McUtils;
import com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController;
import com.qianjh.ryzen.service.RolePermissionService;
import com.qianjh.ryzen.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "角色")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Role_TenantAppController extends _TenantAppController {

    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    @Operation(summary = "列表")
    @GetMapping("/roles")
    public Resp<List<GetRolesResp>> get(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                        @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                        @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                        //
                                        @RequestParam(required = false) String title,
                                        @RequestParam(required = false) Boolean enabled) {
        List<Role> entities = roleService.list(new LambdaQueryWrapper<Role>()
                .eq(Role::getOemId, oemId)
                .eq(Role::getTenantId, tenantId)
                .eq(StringUtils.isNotBlank(title), Role::getTitle, title)
                .eq(enabled != null, Role::getEnabled, enabled)
                .orderByAsc(Role::getId)
        );

        List<GetRolesResp> result = entities.stream()
                .map(e -> GetRolesResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .title(e.getTitle())
                        .enabled(e.getEnabled())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                )
                .toList();

        return Resp.successOf(result);

    }

    @Operation(summary = "创建")
    @PostMapping("/role")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                              //
                              @RequestBody @Validated PostRoleReq body) {
        Role entity = roleService.getByUk(oemId, tenantId, body.getTitle());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }
        entity = roleService.create(oemId, tenantId, body);

        // 创建角色-权限
        Set<Long> permissionIds = body.getPermissionIds();
        if (!CollectionUtils.isEmpty(permissionIds)) {
            for (Long permissionId : permissionIds) {
                rolePermissionService.createIfAbsent(oemId, tenantId, entity.getId(), permissionId);
            }
        }

        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "修改")
    @PatchMapping("/role/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         //
                         @PathVariable Long id,
                         @RequestBody @Validated PatchRoleReq body) {
        Role entity = roleService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = roleService.patch(oemId, tenantId, id, body);

        // 修改权限
        Set<Long> permissionIds = body.getPermissionIds();
        if(permissionIds != null) {
            rolePermissionService.putPermissions(oemId, tenantId, entity.getId(), permissionIds);
        }

        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/role/{id}")
    public Resp<?> delete(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          //
                          @PathVariable Long id) {
        long rolePermissionsCount = rolePermissionService.count(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getOemId, oemId)
                .eq(RolePermission::getTenantId, tenantId)
                .eq(RolePermission::getRoleId, id)
        );
        if (rolePermissionsCount > 0) {
            return Resp.failure(McUtils.i18n(RespMc.RELATION_DATA_EXIST));
        }
        boolean success = roleService.delete(oemId, tenantId, id);
        return success ? Resp.success() : Resp.failure();
    }
}
