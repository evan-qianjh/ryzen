package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetReceptionAccountsResp;
import com.qianjh.ryzen.entity.ReceptionAccount;
import com.qianjh.ryzen.enums.ReceptionAccountStatus;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.mapper.ReceptionAccountMapper;
import com.qianjh.ryzen.service.ReceptionAccountService;
import com.qianjh.ryzen.service.dto.TenantPatchReceptionAccountReq;
import com.qianjh.ryzen.service.dto.TenantPostReceptionAccountReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "接待账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ReceptionAccount_TenantAppController extends _TenantAppController {

    private final ReceptionAccountMapper receptionAccountMapper;
    private final ReceptionAccountService receptionAccountService;

    @Operation(summary = "分页查询")
    @GetMapping("/reception-accounts")
    public Resp<OffsetPage<GetReceptionAccountsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                           @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                           @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long _accountId,
                                                           @RequestParam(required = false) Long accountId,
                                                           @RequestParam(required = false) ReceptionAccountStatus status,
                                                           //
                                                           @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                           @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        Page<ReceptionAccount> page = receptionAccountMapper.selectJoinPage(new Page<>(pageIndex, pageSize), new MPJLambdaWrapper<ReceptionAccount>()
                .eq(ReceptionAccount::getOemId, oemId)
                .eq(ReceptionAccount::getTenantId, tenantId)
                .eq(accountId != null, ReceptionAccount::getAccountId, accountId)
                .eq(status != null, ReceptionAccount::getStatus, status)
                .orderByDesc(ReceptionAccount::getId)
        );
        if (page.getRecords().isEmpty()) {
            return Resp.successOf(PageUtils.wrap(page, Collections.emptyList()));
        }

        //
        List<GetReceptionAccountsResp> records = page.getRecords().stream()
                .map(e -> GetReceptionAccountsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .accountId(IdUtils.toString(e.getAccountId()))
                        .status(e.getStatus())
                        .remark(e.getRemark())
                        .build()
                ).toList();
        OffsetPage<GetReceptionAccountsResp> result = PageUtils.wrap(page, records);
        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/reception-account")
    public Resp<?> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestBody @Validated TenantPostReceptionAccountReq body) {
        // TODO
        return null;
    }

    @Operation(summary = "修改")
    @PatchMapping("/reception-account/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @PathVariable Long id,
                         @RequestBody @Validated TenantPatchReceptionAccountReq body) {
        // TODO
        return null;
    }
}
