package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetPermissionsResp;
import com.qianjh.ryzen.entity.Permission;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController;
import com.qianjh.ryzen.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "权限")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Permission_TenantAppController extends _TenantAppController {

    private final PermissionService permissionService;

    @Operation(summary = "列表")
    @GetMapping("/permissions")
    public Resp<List<GetPermissionsResp>> get(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                              //
                                              @RequestParam(required = false) Boolean enabled) {
        List<Permission> entities = permissionService.list(new LambdaQueryWrapper<Permission>()
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
}
