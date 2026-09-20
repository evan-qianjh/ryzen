package com.qianjh.ryzen.controller.admin;


import com.qianjh.ryzen.framework.common.api.ClientInfo;
import com.qianjh.ryzen.framework.common.api.Resp;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.framework.service.controller.admin._AdminController;
import com.qianjh.ryzen.framework.servlet.service.HttpRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.qianjh.ryzen.framework.service.controller.admin._AdminController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户Token")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class AccountToken_AdminController extends _AdminController {

    private final HttpRequestService httpRequestService;

    @Operation(summary = "退出")
    @DeleteMapping("/account-token/{id}")
    public Resp<?> create(HttpServletRequest request,
                          @RequestHeader(GatewayHeaderAdmin.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderAdmin.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderAdmin.ACCOUNT_ID) Long accountId,
                          @PathVariable Long id) {

        ClientInfo clientInfo = httpRequestService.getClientInfo(request);

        // todo
        log.info("logout ::: path={}, clientInfo={}", request.getServletPath(), clientInfo);

        return Resp.success();
    }

}
