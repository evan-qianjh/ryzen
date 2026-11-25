package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetMatchTagImAccountsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostMatchTagImAccountReq;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagImAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.MatchTagService;
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
@Tag(name = "匹配标签-IM账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class MatchTagImAccount_TenantAppController extends _TenantAppController {

    private final MatchTagService matchTagService;
    private final ImAccountService imAccountService;
    private final MatchTagImAccountService matchTagImAccountService;

    @Operation(summary = "列表查询")
    @GetMapping("/match-tag-im-accounts")
    public Resp<List<?>> list(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                              @RequestParam(required = false) Long imAccountId,
                              @RequestParam(required = false) Long matchTagId) {
        if (ObjectUtils.allNull(imAccountId, matchTagId)) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        List<MatchTagImAccount> entities = matchTagImAccountService.list(new LambdaQueryWrapper<MatchTagImAccount>()
                .eq(MatchTagImAccount::getOemId, oemId)
                .eq(MatchTagImAccount::getTenantId, tenantId)
                .eq(imAccountId != null, MatchTagImAccount::getImAccountId, imAccountId)
                .eq(matchTagId != null, MatchTagImAccount::getMatchTagId, matchTagId)
        );

        List<GetMatchTagImAccountsResp> result = entities.stream()
                .map(e ->
                        GetMatchTagImAccountsResp.builder()
                                .id(IdUtils.toString(e.getId()))
                                .matchTagId(IdUtils.toString(e.getMatchTagId()))
                                .imAccountId(IdUtils.toString(e.getImAccountId()))
                                .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                                .build()
                ).toList();

        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/match-tag-im-account")
    public Resp<Receipt> post(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                              @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                              @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                              @RequestBody @Validated PostMatchTagImAccountReq body) {
        Operator operator = buildOperator(accountId, accountName);
        //
        MatchTag matchTag = matchTagService.getById(oemId, tenantId, body.matchTagId());
        if (matchTag == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        //
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, body.imAccountId());
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }

        // entity
        MatchTagImAccount entity = matchTagImAccountService.getByUk(oemId, tenantId, matchTag.getId(), imAccount.getId());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }

        entity = matchTagImAccountService.create(matchTag, imAccount, operator);

        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "删除")
    @DeleteMapping("/match-tag-im-account/{id}")
    public Resp<?> delete(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                          @PathVariable Long id) {
        Operator operator = buildOperator(accountId, accountName);

        MatchTagImAccount entity = matchTagImAccountService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = matchTagImAccountService.delete(entity, operator);
        return success ? Resp.success() : Resp.failure();
    }
}
