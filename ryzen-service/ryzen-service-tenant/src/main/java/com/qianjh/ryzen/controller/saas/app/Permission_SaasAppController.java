package com.qianjh.ryzen.controller.saas.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetPermissionsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PatchPermissionReq;
import com.qianjh.ryzen.controller.tenant.app.dto.PostPermissionReq;
import com.qianjh.ryzen.entity.Permission;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderSaas;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.util.McUtils;
import com.qianjh.ryzen.framework.service.controller.saas._SaasAppController;
import com.qianjh.ryzen.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.service.controller.saas._SaasAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "权限")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Permission_SaasAppController extends _SaasAppController {

    private final PermissionService permissionService;

    @Operation(summary = "列表")
    @GetMapping("/permissions")
    public Resp<List<GetPermissionsResp>> get(@RequestHeader(GatewayHeaderSaas.ACCOUNT_ID) Long accountId,
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

    @Operation(summary = "创建")
    @PostMapping("/permission")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderSaas.ACCOUNT_ID) Long accountId,
                              //
                              @RequestBody @Validated PostPermissionReq body) {
        Long parentId = body.getParentId();
        if (parentId != null) {
            Permission parent = permissionService.getById(parentId);
            if (parent == null) {
                return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
            }
        }

        Permission entity = permissionService.getByUk(body.getSymbol());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }
        entity = permissionService.create(body);
        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "修改")
    @PatchMapping("/permission/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderSaas.ACCOUNT_ID) Long accountId,
                         //
                         @PathVariable Long id,
                         @RequestBody @Validated PatchPermissionReq body) {
        if (body.getParentId() != null && body.getParentId().equals(id)) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        Permission entity = permissionService.getById(id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = permissionService.patch(id, body);

        return success ? Resp.success() : Resp.failure();
    }
}
