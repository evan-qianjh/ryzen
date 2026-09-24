package com.qianjh.ryzen.controller.tenant;


import com.qianjh.ryzen.framework.common.dto.ClientInfo;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.service.controller.tenant._TenantController;
import com.qianjh.ryzen.framework.servlet.service.HttpRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户Token")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class AccountToken_TenantController extends _TenantController {

    private final HttpRequestService httpRequestService;

    @Operation(summary = "退出")
    @DeleteMapping("/account-token/{id}")
    public Resp<?> create(HttpServletRequest request,
                          @RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          //
                          @PathVariable Long id) {

        ClientInfo clientInfo = httpRequestService.getClientInfo(request);

        // todo
        log.info("logout ::: path={}, clientInfo={}", request.getServletPath(), clientInfo);

        return Resp.success();
    }

}
