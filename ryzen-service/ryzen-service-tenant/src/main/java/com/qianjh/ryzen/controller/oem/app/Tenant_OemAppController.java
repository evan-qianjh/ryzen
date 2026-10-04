package com.qianjh.ryzen.controller.oem.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.controller.oem.app.dto.GetTenantsResp;
import com.qianjh.ryzen.entity.Tenant;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderOem;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.service.controller.oem._OemAppController;
import com.qianjh.ryzen.framework.service.util.PageUtils;
import com.qianjh.ryzen.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "租户")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Tenant_OemAppController extends _OemAppController {

    private final TenantService tenantService;

    @Operation(summary = "分页查询")
    @GetMapping("/tenants")
    public Resp<OffsetPage<GetTenantsResp>> page(@RequestHeader(GatewayHeaderOem.OEM_ID) Long oemId,
                                                 @RequestHeader(GatewayHeaderOem.ACCOUNT_ID) Long accountId,
                                                 //
                                                 @RequestParam(required = false) Boolean enabled,
                                                 @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                 @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Max(100) Integer pageSize) {
        Page<Tenant> page = tenantService.page(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getOemId, oemId)
                .eq(enabled != null, Tenant::getEnabled, enabled)
                .orderByDesc(Tenant::getId)
        );

        List<Tenant> _records = page.getRecords();

        List<GetTenantsResp> records = _records.stream()
                .map(e -> GetTenantsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .symbol(e.getSymbol())
                        .enabled(e.getEnabled())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                ).toList();

        return Resp.successOf(PageUtils.wrap(page, records));
    }
}
