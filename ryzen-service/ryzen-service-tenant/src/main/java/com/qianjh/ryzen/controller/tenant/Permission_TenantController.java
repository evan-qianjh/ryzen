package com.qianjh.ryzen.controller.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.dto.GetPermissionsResp;
import com.qianjh.ryzen.controller.tenant.dto.PatchPermissionReq;
import com.qianjh.ryzen.controller.tenant.dto.PostPermissionReq;
import com.qianjh.ryzen.entity.Permission;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.util.McUtils;
import com.qianjh.ryzen.framework.service.controller.tenant._TenantController;
import com.qianjh.ryzen.service.PermissionService;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantController.PATH_PREFIX;

@Slf4j
@Tag(name = "权限")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Permission_TenantController extends _TenantController {

    private final PermissionService permissionService;

    @Operation(summary = "列表")
    @GetMapping("/permissions")
    public Resp<List<GetPermissionsResp>> get(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                              //
                                              @RequestParam(required = false) Boolean enabled) {
        List<Permission> entities = permissionService.list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getOemId, oemId)
                .eq(Permission::getTenantId, tenantId)
                .eq(enabled != null, Permission::getEnabled, enabled)
                .orderByAsc(Permission::getId)
        );

        List<GetPermissionsResp> result = entities.stream()
                .map(e -> GetPermissionsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .parentId(IdUtils.toString(e.getParentId()))
                        .type(e.getType())
                        .title(e.getTitle())
                        .symbol(e.getSymbol())
                        .enabled(e.getEnabled())
                        .build()
                )
                .toList();

        return Resp.successOf(result);

    }

    @Operation(summary = "创建")
    @PostMapping("/permission")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                              //
                              @RequestBody @Validated PostPermissionReq body) {
        Long parentId = body.getParentId();
        if (parentId != null) {
            Permission parent = permissionService.getById(oemId, tenantId, parentId);
            if (parent == null) {
                return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
            }
        }

        Permission entity = permissionService.getByUk(oemId, tenantId, body.getSymbol());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }
        entity = permissionService.create(oemId, tenantId, body);
        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "修改")
    @PatchMapping("/permission/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         //
                         @PathVariable Long id,
                         @RequestBody @Validated PatchPermissionReq body) {
        if (body.getParentId() != null && body.getParentId().equals(id)) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        Permission entity = permissionService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = permissionService.patch(oemId, tenantId, id, body);

        return success ? Resp.success() : Resp.failure();
    }
}
