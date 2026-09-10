package com.qianjh.ryzen.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.admin.dto.GetRolesResp;
import com.qianjh.ryzen.controller.admin.dto.PatchRoleReq;
import com.qianjh.ryzen.controller.admin.dto.PostRoleReq;
import com.qianjh.ryzen.entity.Role;
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
@Tag(name = "角色")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Role_AdminController extends _AdminController {

    private final RoleService roleService;

    @Operation(summary = "列表")
    @GetMapping("/roles")
    public Resp<List<GetRolesResp>> get(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                                        @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                                        @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                                        @RequestParam(required = false) Boolean enabled) {
        List<Role> entities = roleService.list(new LambdaQueryWrapper<Role>()
                .eq(Role::getTenantId, tenantId)
                .eq(enabled != null, Role::getEnabled, enabled)
                .orderByAsc(Role::getId)
        );

        List<GetRolesResp> result = entities.stream()
                .map(e -> GetRolesResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .title(e.getTitle())
                        .enabled(e.getEnabled())
                        .build()
                )
                .toList();

        return Resp.successOf(result);

    }

    @Operation(summary = "创建")
    @PostMapping("/role")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                              @RequestBody @Validated PostRoleReq body) {
        Role entity = roleService.getByUk(body.getTitle(), tenantId);
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }
        entity = roleService.create(body, tenantId);
        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "修改")
    @PatchMapping("/role/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                         @PathVariable Long id,
                         @RequestBody @Validated PatchRoleReq body) {
        Role entity = roleService.getById(id, tenantId);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = roleService.patch(id, body, tenantId);

        return success ? Resp.success() : Resp.failure();
    }
}
