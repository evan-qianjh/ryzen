package com.qianjh.ryzen.controller.tenant.app;

import com.qianjh.ryzen.controller.tenant.app.dto.PostMatchTagReceptionAccountReq;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "匹配标签-接待账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class MatchTagReceptionAccount_TenantAppController extends _TenantAppController {

    @Operation(summary = "列表查询")
    @GetMapping("/match-tag-reception-accounts")
    public Resp<List<?>> list(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long _accountId,
                              @RequestParam(required = false) Long accountId,
                              @RequestParam(required = false) Long tagId) {
        if (ObjectUtils.allNull(accountId, tagId)) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        // TODO
        return null;
    }

    @Operation(summary = "创建")
    @PostMapping("/match-tag-reception-account")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long _accountId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String _accountName,
                              @RequestBody @Validated PostMatchTagReceptionAccountReq body) {
        // TODO
        return null;
    }

    @Operation(summary = "删除")
    @DeleteMapping("/match-tag-reception-account/{id}")
    public Resp<?> delete(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long _accountId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String _accountName,
                          @PathVariable Long id) {
        // TODO
        return null;
    }
}
