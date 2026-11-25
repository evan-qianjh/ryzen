package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetMatchTagsResp;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import com.qianjh.ryzen.ims.enums.MatchTagStatus;
import com.qianjh.ryzen.service.MatchTagService;
import com.qianjh.ryzen.service.dto.TenantPatchMatchTagReq;
import com.qianjh.ryzen.service.dto.TenantPostMatchTagReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "匹配标签")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class MatchTag_TenantAppController extends _TenantAppController {

    private final MatchTagService matchTagService;

    @Operation(summary = "分页查询")
    @GetMapping("/match-tags")
    public Resp<List<GetMatchTagsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                             @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                             @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                             @RequestParam(required = false) String tag,
                                             @RequestParam(required = false) String group,
                                             @RequestParam(required = false) MatchTagStatus status) {
        List<MatchTag> entities = matchTagService.list(new LambdaQueryWrapper<MatchTag>()
                .eq(MatchTag::getOemId, oemId)
                .eq(MatchTag::getTenantId, tenantId)
                .eq(StringUtils.isNotBlank(tag), MatchTag::getTag, tag)
                .eq(StringUtils.isNotBlank(group), MatchTag::getGroupName, group)
                .eq(status != null, MatchTag::getStatus, status)
                .orderByDesc(MatchTag::getId)
        );

        List<GetMatchTagsResp> result = entities.stream()
                .map(e -> GetMatchTagsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .tag(e.getTag())
                        .group(e.getGroupName())
                        .status(e.getStatus())
                        .build()
                )
                .toList();
        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/match-tag")
    public Resp<Receipt> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                @RequestBody @Validated TenantPostMatchTagReq body) {
        MatchTag entity = matchTagService.getByUk(oemId, tenantId, body.tag());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }
        entity = matchTagService.create(oemId, tenantId, body);

        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "修改")
    @PatchMapping("/match-tag/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @PathVariable Long id,
                         @RequestBody @Validated TenantPatchMatchTagReq body) {
        MatchTag entity = matchTagService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = matchTagService.patch(entity, body);
        return success ? Resp.success() : Resp.failure();
    }
}
