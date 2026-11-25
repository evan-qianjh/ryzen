package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetServiceAccountsResp;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.enums.ServiceAccountStatus;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.mapper.ServiceAccountMapper;
import com.qianjh.ryzen.service.ServiceAccountService;
import com.qianjh.ryzen.service.dto.TenantPatchServiceAccountReq;
import com.qianjh.ryzen.service.dto.TenantPostServiceAccountReq;
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
@Tag(name = "服务账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ServiceAccount_TenantAppController extends _TenantAppController {

    private final ServiceAccountMapper serviceAccountMapper;
    private final ServiceAccountService serviceAccountService;

    @Operation(summary = "分页查询")
    @GetMapping("/service-accounts")
    public Resp<OffsetPage<GetServiceAccountsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long _accountId,
                                                         @RequestParam(required = false) Long accountId,
                                                         @RequestParam(required = false) ServiceAccountStatus status,
                                                         //
                                                         @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                         @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        Page<ServiceAccount> page = serviceAccountMapper.selectJoinPage(new Page<>(pageIndex, pageSize), new MPJLambdaWrapper<ServiceAccount>()
                .eq(ServiceAccount::getOemId, oemId)
                .eq(ServiceAccount::getTenantId, tenantId)
                .eq(accountId != null, ServiceAccount::getAccountId, accountId)
                .eq(status != null, ServiceAccount::getStatus, status)
                .orderByDesc(ServiceAccount::getId)
        );
        if (page.getRecords().isEmpty()) {
            return Resp.successOf(PageUtils.wrap(page, Collections.emptyList()));
        }

        //
        List<GetServiceAccountsResp> records = page.getRecords().stream()
                .map(e -> GetServiceAccountsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .accountId(IdUtils.toString(e.getAccountId()))
                        .status(e.getStatus())
                        .remark(e.getRemark())
                        .build()
                ).toList();
        OffsetPage<GetServiceAccountsResp> result = PageUtils.wrap(page, records);
        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/service-account")
    public Resp<?> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestBody @Validated TenantPostServiceAccountReq body) {
        // TODO
        return null;
    }

    @Operation(summary = "修改")
    @PatchMapping("/service-account/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @PathVariable Long id,
                         @RequestBody @Validated TenantPatchServiceAccountReq body) {
        // TODO
        return null;
    }
}
