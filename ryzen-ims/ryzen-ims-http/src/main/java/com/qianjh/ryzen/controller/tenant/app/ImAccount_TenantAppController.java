package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.controller.tenant.app.dto.GetImAccountsResp;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.enums.ImAccountStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.enums.SchedulerCommandType;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.framework.stream.util.StreamUtils;
import com.qianjh.ryzen.model.SchedulerCommand;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.stream.ImsSchedulerCommandTopicStreamProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "IM账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ImAccount_TenantAppController extends _TenantAppController {

    private final ImAccountService imAccountService;
    private final ImsSchedulerCommandTopicStreamProducer imsSchedulerCommandTopicStreamProducer;

    @Operation(summary = "分页查询")
    @GetMapping("/im-accounts")
    public Resp<OffsetPage<GetImAccountsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                    @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                    @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                    //
                                                    @RequestParam(required = false) String code,
                                                    @RequestParam(required = false) ImType imType,
                                                    @RequestParam(required = false) ImAccountStatus status,
                                                    //
                                                    @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                    @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        Page<ImAccount> page = imAccountService.page(new Page<>(pageIndex, safePageSize(pageSize)), new LambdaQueryWrapper<ImAccount>()
                .eq(ImAccount::getOemId, oemId)
                .eq(ImAccount::getTenantId, tenantId)
                .eq(StringUtils.hasText(code), ImAccount::getCode, code)
                .eq(imType != null, ImAccount::getImType, imType)
                .eq(status != null, ImAccount::getStatus, status)
                .orderByDesc(ImAccount::getId)
        );

        if (page.getRecords().isEmpty()) {
            return Resp.successOf(PageUtils.wrap(page, Collections.emptyList()));
        }

        List<GetImAccountsResp> records = page.getRecords().stream()
                .map(e -> GetImAccountsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .code(e.getCode())
                        .imType(e.getImType())
                        .status(e.getStatus())
                        .remark(e.getRemark())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                ).toList();

        OffsetPage<GetImAccountsResp> result = PageUtils.wrap(page, records);
        return Resp.successOf(result);
    }

    @Operation(summary = "启动")
    @PostMapping("/im-account/{id}/start")
    public Resp<?> start(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @PathVariable Long id) {
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, id);
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        // 判断状态
        if (imAccount.getStatus() != ImAccountStatus.STOPPED) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_CONFLICT));
        }

        // command
        SchedulerCommand message = SchedulerCommand.builder()
                .code(UUID.randomUUID().toString().replace("-", ""))
                .type(SchedulerCommandType.ASSIGN)
                .imType(imAccount.getImType())
                .accountCode(imAccount.getCode())
                .build();
        imsSchedulerCommandTopicStreamProducer.send(
                MessageBuilder.withPayload(StreamUtils.toJson(message)).build()
        );

        // STARTING
        boolean success = imAccountService.updateStatus(imAccount, imAccount.getStatus(), ImAccountStatus.STARTING);

        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "停止")
    @PostMapping("/im-account/{id}/stop")
    public Resp<?> stop(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                        @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                        @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                        @PathVariable Long id) {
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, id);
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        SchedulerCommand message = SchedulerCommand.builder()
                .code(UUID.randomUUID().toString().replace("-", ""))
                .type(SchedulerCommandType.RELEASE)
                .imType(imAccount.getImType())
                .accountCode(imAccount.getCode())
                .build();
        imsSchedulerCommandTopicStreamProducer.send(
                MessageBuilder.withPayload(StreamUtils.toJson(message)).build()
        );
        return Resp.success();
    }

}
