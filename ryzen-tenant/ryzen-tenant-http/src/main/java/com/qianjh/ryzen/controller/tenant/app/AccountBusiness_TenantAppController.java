package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetAccountBusinessResp;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountBusiness;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.mapper.AccountBusinessMapper;
import com.qianjh.ryzen.service.AccountBusinessService;
import com.qianjh.ryzen.service.dto.TenantPatchAccountBusinessReq;
import com.qianjh.ryzen.service.dto.TenantPostAccountBusinessReq;
import com.qianjh.ryzen.tenant.enums.AccountBusinessStatus;
import com.qianjh.ryzen.tenant.enums.AccountBusinessType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "账户业务")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class AccountBusiness_TenantAppController extends _TenantAppController {

    private final AccountBusinessMapper accountBusinessMapper;
    private final AccountBusinessService accountBusinessService;


    @Data
    @EqualsAndHashCode(callSuper = true)
    static class GetAccountBusinessView extends AccountBusiness {
        private String username;
        private String nickname;
    }

    @Operation(summary = "分页查询")
    @GetMapping("/account-business")
    public Resp<OffsetPage<GetAccountBusinessResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long _accountId,
                                                         @RequestParam(required = false) Long accountId,
                                                         @RequestParam(required = false) String username,
                                                         @RequestParam(required = false) AccountBusinessType type,
                                                         @RequestParam(required = false) AccountBusinessStatus status,
                                                         //
                                                         @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                         @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        Page<GetAccountBusinessView> page = accountBusinessMapper.selectJoinPage(new Page<>(pageIndex, pageSize), GetAccountBusinessView.class,
                new MPJLambdaWrapper<AccountBusiness>()
                        .selectAll(AccountBusiness.class)
                        .selectAs(Account::getUsername, GetAccountBusinessView::getUsername)
                        .selectAs(Account::getNickname, GetAccountBusinessView::getNickname)
                        // join
                        .leftJoin(Account.class, Account::getId, AccountBusiness::getAccountId)
                        // where
                        .eq(AccountBusiness::getOemId, oemId)
                        .eq(AccountBusiness::getTenantId, tenantId)
                        .eq(accountId != null, AccountBusiness::getAccountId, accountId)
                        .eq(type != null, AccountBusiness::getType, type)
                        .eq(status != null, AccountBusiness::getStatus, status)
                        .orderByDesc(AccountBusiness::getId)
        );
        if (page.getRecords().isEmpty()) {
            return Resp.successOf(PageUtils.wrap(page, Collections.emptyList()));
        }

        //
        List<GetAccountBusinessResp> records = page.getRecords().stream()
                .map(e -> GetAccountBusinessResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .accountId(IdUtils.toString(e.getAccountId()))
                        .account(GetAccountBusinessResp.Account.builder()
                                .username(e.username)
                                .nickname(e.nickname)
                                .build())
                        .status(e.getStatus())
                        .type(e.getType())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                ).toList();
        OffsetPage<GetAccountBusinessResp> result = PageUtils.wrap(page, records);
        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/account-busines")
    public Resp<Receipt> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                                @RequestBody @Validated TenantPostAccountBusinessReq body) {
        Operator operator = buildOperator(accountId, accountName);

        //
        AccountBusiness entity = accountBusinessService.getByUk(oemId, tenantId, body.accountId(), body.type());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }

        entity = accountBusinessService.create(oemId, tenantId, body, operator);

        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "修改")
    @PatchMapping("/account-busines/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                         @PathVariable Long id,
                         @RequestBody @Validated TenantPatchAccountBusinessReq body) {
        Operator operator = buildOperator(accountId, accountName);

        AccountBusiness entity = accountBusinessService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = accountBusinessService.patch(entity, body, operator);
        return success ? Resp.success() : Resp.failure();
    }
}
