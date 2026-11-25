package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetMatchTagServiceAccountsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostMatchTagServiceAccountReq;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagServiceAccount;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import com.qianjh.ryzen.service.MatchTagService;
import com.qianjh.ryzen.service.MatchTagServiceAccountService;
import com.qianjh.ryzen.service.ServiceAccountService;
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
@Tag(name = "匹配标签-服务账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class MatchTagServiceAccount_TenantAppController extends _TenantAppController {

    private final MatchTagService matchTagService;
    private final ServiceAccountService serviceAccountService;
    private final MatchTagServiceAccountService matchTagServiceAccountService;

    @Operation(summary = "列表查询")
    @GetMapping("/match-tag-service-accounts")
    public Resp<List<?>> list(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                              @RequestParam(required = false) Long serviceAccountId,
                              @RequestParam(required = false) Long matchTagId) {
        if (ObjectUtils.allNull(serviceAccountId, matchTagId)) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        List<MatchTagServiceAccount> entities = matchTagServiceAccountService.list(new LambdaQueryWrapper<MatchTagServiceAccount>()
                .eq(MatchTagServiceAccount::getOemId, oemId)
                .eq(MatchTagServiceAccount::getTenantId, tenantId)
                .eq(serviceAccountId != null, MatchTagServiceAccount::getServiceAccountId, serviceAccountId)
                .eq(matchTagId != null, MatchTagServiceAccount::getMatchTagId, matchTagId)
        );

        List<GetMatchTagServiceAccountsResp> result = entities.stream()
                .map(e ->
                        GetMatchTagServiceAccountsResp.builder()
                                .id(IdUtils.toString(e.getId()))
                                .matchTagId(IdUtils.toString(e.getMatchTagId()))
                                .serviceAccountId(IdUtils.toString(e.getServiceAccountId()))
                                .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                                .build()
                ).toList();

        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/match-tag-service-account")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                              @RequestBody @Validated PostMatchTagServiceAccountReq body) {
        Operator operator = buildOperator(accountId, accountName);
        //
        MatchTag matchTag = matchTagService.getById(oemId, tenantId, body.matchTagId());
        if (matchTag == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        //
        ServiceAccount serviceAccount = serviceAccountService.getById(oemId, tenantId, body.serviceAccountId());
        if (serviceAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }

        // entity
        MatchTagServiceAccount entity = matchTagServiceAccountService.getByUk(oemId, tenantId, matchTag.getId(), serviceAccount.getId());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }

        entity = matchTagServiceAccountService.create(matchTag, serviceAccount, operator);

        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/match-tag-service-account/{id}")
    public Resp<?> delete(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                          @PathVariable Long id) {
        Operator operator = buildOperator(accountId, accountName);

        MatchTagServiceAccount entity = matchTagServiceAccountService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = matchTagServiceAccountService.delete(entity, operator);
        return success ? Resp.success() : Resp.failure();
    }
}
