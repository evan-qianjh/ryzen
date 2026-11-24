package com.qianjh.ryzen.controller.tenant.app;

import com.qianjh.ryzen.controller.tenant.app.dto.PatchReceptionAccountReq;
import com.qianjh.ryzen.controller.tenant.app.dto.PostReceptionAccountReq;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "接待账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ReceptionAccount_TenantAppController extends _TenantAppController {

    @Operation(summary = "分页查询")
    @GetMapping("/reception-accounts")
    public Resp<OffsetPage<?>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                    @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                    @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                    @RequestParam(required = false) String username,
                                    //
                                    @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                    @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        // TODO
        return null;
    }

    @Operation(summary = "创建")
    @PostMapping("/reception-account")
    public Resp<?> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestBody @Validated PostReceptionAccountReq body) {
        // TODO
        return null;
    }

    @Operation(summary = "修改")
    @PatchMapping("/reception-account/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @PathVariable Long id,
                         @RequestBody @Validated PatchReceptionAccountReq body) {
        // TODO
        return null;
    }
}
