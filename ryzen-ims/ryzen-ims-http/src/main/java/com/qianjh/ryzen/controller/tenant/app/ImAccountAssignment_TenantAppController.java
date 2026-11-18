package com.qianjh.ryzen.controller.tenant.app;

import com.qianjh.ryzen.controller.tenant.app.dto.PostImAccountAssignmentReq;
import com.qianjh.ryzen.framework.common.dto.Receipt;
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
@Tag(name = "IM账号分配")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ImAccountAssignment_TenantAppController extends _TenantAppController {

    @Operation(summary = "创建")
    @PostMapping("/im-account-assignment")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                              @RequestBody @Validated PostImAccountAssignmentReq body) {
        // TODO
        return Resp.successOf(null);
    }
}
