package com.qianjh.ryzen.controller.saas.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.controller.saas.app.dto.GetOemsResp;
import com.qianjh.ryzen.entity.Oem;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderSaas;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.service.controller.saas._SaasAppController;
import com.qianjh.ryzen.framework.service.util.PageUtils;
import com.qianjh.ryzen.service.OemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.service.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "OEM")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Oem_SaasAppController extends _SaasAppController {

    private final OemService oemService;

    @Operation(summary = "分页查询")
    @GetMapping("/tenants")
    public Resp<OffsetPage<GetOemsResp>> page(@RequestHeader(GatewayHeaderSaas.ACCOUNT_ID) Long accountId,
                                              //
                                              @RequestParam(required = false) Boolean enabled,
                                              @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                              @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Max(100) Integer pageSize) {
        Page<Oem> page = oemService.page(new Page<>(pageIndex, pageSize), new LambdaQueryWrapper<Oem>()
                .eq(enabled != null, Oem::getEnabled, enabled)
                .orderByDesc(Oem::getId)
        );

        List<Oem> _records = page.getRecords();

        List<GetOemsResp> records = _records.stream()
                .map(e -> GetOemsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .symbol(e.getSymbol())
                        .enabled(e.getEnabled())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                ).toList();

        return Resp.successOf(PageUtils.wrap(page, records));
    }
}
