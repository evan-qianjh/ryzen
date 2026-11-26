package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianjh.ryzen.controller.tenant.app.dto.GetImAccountsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostMatchTagsByDomainReq;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.ToSchedulerCommand;
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
import com.qianjh.ryzen.ims.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.ims.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.SchedulerCommandType;
import com.qianjh.ryzen.ims.stream.domain.command.type.AppToSchedulerCommandRequest;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.MatchTagService;
import com.qianjh.ryzen.service.ToSchedulerCommandService;
import com.qianjh.ryzen.service.dto.TenantPostImAccountReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "IM账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ImAccount_TenantAppController extends _TenantAppController {

    private final MatchTagService matchTagService;
    private final ImAccountService imAccountService;
    private final ToSchedulerCommandService imAccountCommandService;
    private final MatchTagImAccountService matchTagImAccountService;
    private final AppToSchedulerCommandRequest.Publisher appToSchedulerCommandRequestPublisher;

    @Operation(summary = "分页查询")
    @GetMapping("/im-accounts")
    public Resp<OffsetPage<GetImAccountsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                    @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                    @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                    //
                                                    @RequestParam(required = false) String code,
                                                    @RequestParam(required = false) ImType imType,
                                                    @RequestParam(required = false) ImAccountAssignStatus assignStatus,
                                                    @RequestParam(required = false) ImAccountExecutorStatus executorStatus,
                                                    //
                                                    @RequestParam(required = false, defaultValue = DEFAULT_PAGE_INDEX) Integer pageIndex,
                                                    @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize) {
        Page<ImAccount> page = imAccountService.page(new Page<>(pageIndex, safePageSize(pageSize)), new LambdaQueryWrapper<ImAccount>()
                .eq(ImAccount::getOemId, oemId)
                .eq(ImAccount::getTenantId, tenantId)
                .eq(StringUtils.hasText(code), ImAccount::getCode, code)
                .eq(imType != null, ImAccount::getImType, imType)
                .eq(assignStatus != null, ImAccount::getAssignStatus, assignStatus)
                .eq(executorStatus != null, ImAccount::getExecutorStatus, executorStatus)
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
                        .assignStatus(e.getAssignStatus())
                        .executorStatus(e.getExecutorStatus())
                        .remark(e.getRemark())
                        .createdTime(DateTimeUtils.getTime(e.getCreatedTime()))
                        .build()
                ).toList();

        OffsetPage<GetImAccountsResp> result = PageUtils.wrap(page, records);
        return Resp.successOf(result);
    }

    @Operation(summary = "创建")
    @PostMapping("/im-account")
    public Resp<?> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                          @RequestBody @Validated TenantPostImAccountReq body) {
        ImAccount entity = imAccountService.getByUk(body.imType(), body.accountCode());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }
        Operator operator = buildOperator(accountId, accountName);

        entity = imAccountService.create(oemId, tenantId, body, operator);

        // 如果指定了标签，则直接创建
        if (body.matchTagIds() != null && !body.matchTagIds().isEmpty()) {
            List<MatchTag> matchTags = matchTagService.listByIds(oemId, tenantId, body.matchTagIds());
            for (MatchTag matchTag : matchTags) {
                matchTagImAccountService.createIfAbsent(matchTag, entity, operator);
            }
        }

        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "分配")
    @PostMapping("/im-account/{id}/assign")
    public Resp<?> assign(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                          @PathVariable Long id) {
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, id);
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        // 判断状态
        if (imAccount.getAssignStatus() != ImAccountAssignStatus.NONE && imAccount.getAssignStatus() != ImAccountAssignStatus.RELEASED) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_CONFLICT));
        }

        Operator operator = buildOperator(accountId, accountName);

        SchedulerCommandType commandType = SchedulerCommandType.ASSIGN_EXECUTOR;

        // 创建命令
        ToSchedulerCommand command = imAccountCommandService.create(imAccount, commandType);

        // command
        appToSchedulerCommandRequestPublisher.publish(
                AppToSchedulerCommandRequest.Body.builder()
                        .code(IdUtils.toString(command.getId()))
                        .type(commandType)
                        .imType(imAccount.getImType())
                        .accountCode(imAccount.getCode())
                        .build()
        );

        // ASSIGNING
        boolean success = imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.ASSIGNING, operator);

        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "释放")
    @PostMapping("/im-account/{id}/release")
    public Resp<?> release(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                           @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                           @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                           @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                           @PathVariable Long id) {
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, id);
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        if (imAccount.getExecutorStatus() != ImAccountExecutorStatus.NONE && imAccount.getExecutorStatus() != ImAccountExecutorStatus.STOPPED) {
            return Resp.failure("请先停止执行器");
        }
        // 未配分执行器
        if (imAccount.getExecutorAssignmentId() == null) {
            return Resp.failure("未配分执行器");
        }

        Operator operator = buildOperator(accountId, accountName);

        // 创建命令
        SchedulerCommandType commandType = SchedulerCommandType.RELEASE_EXECUTOR;
        ToSchedulerCommand command = imAccountCommandService.create(imAccount, commandType);
        appToSchedulerCommandRequestPublisher.publish(
                AppToSchedulerCommandRequest.Body.builder()
                        .code(IdUtils.toString(command.getId()))
                        .type(commandType)
                        .imType(imAccount.getImType())
                        .accountCode(imAccount.getCode())
                        .assignmentId(imAccount.getExecutorAssignmentId())
                        .build()
        );

        // RELEASING
        boolean success = imAccountService.updateAssignStatus(imAccount, imAccount.getAssignStatus(), ImAccountAssignStatus.RELEASING, operator);

        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "启动")
    @PostMapping("/im-account/{id}/start")
    public Resp<?> start(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                         @PathVariable Long id) {
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, id);
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        // 判断状态
        if (imAccount.getExecutorStatus() != ImAccountExecutorStatus.NONE
                && imAccount.getExecutorStatus() != ImAccountExecutorStatus.STOPPED) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_CONFLICT));
        }

        // 未配分执行器
        if (imAccount.getExecutorAssignmentId() == null) {
            return Resp.failure("未配分执行器");
        }

        Operator operator = buildOperator(accountId, accountName);

        SchedulerCommandType commandType = SchedulerCommandType.START_EXECUTOR;
        ToSchedulerCommand command = imAccountCommandService.create(imAccount, commandType);

        // command
        appToSchedulerCommandRequestPublisher.publish(
                AppToSchedulerCommandRequest.Body.builder()
                        .code(IdUtils.toString(command.getId()))
                        .type(commandType)
                        .assignmentId(imAccount.getExecutorAssignmentId())
                        .build()
        );

        // STARTING
        boolean success = imAccountService.updateExecutorStatus(imAccount,
                imAccount.getExecutorStatus(), ImAccountExecutorStatus.PENDING_ACCEPT_START, operator
        );

        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "停止")
    @PostMapping("/im-account/{id}/stop")
    public Resp<?> stop(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                        @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                        @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                        @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                        @PathVariable Long id) {
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, id);
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        // 未配分执行器
        if (imAccount.getExecutorAssignmentId() == null) {
            return Resp.failure("未配分执行器");
        }

        Operator operator = buildOperator(accountId, accountName);

        SchedulerCommandType commandType = SchedulerCommandType.STOP_EXECUTOR;
        ToSchedulerCommand command = imAccountCommandService.create(imAccount, commandType);

        // command
        appToSchedulerCommandRequestPublisher.publish(
                AppToSchedulerCommandRequest.Body.builder()
                        .code(IdUtils.toString(command.getId()))
                        .type(commandType)
                        .assignmentId(imAccount.getExecutorAssignmentId())
                        .build()
        );

        //
        if (imAccount.getExecutorStatus() != ImAccountExecutorStatus.STARTED) {
            return Resp.success();
        }

        boolean success = imAccountService.updateExecutorStatus(imAccount, imAccount.getExecutorStatus(), ImAccountExecutorStatus.STOPPING, operator);

        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "创建匹配标签")
    @PostMapping("/im-account/{id}/match-tags")
    public Resp<?> createMatchTags(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                   @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                   @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                   @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                                   @PathVariable Long id,
                                   @RequestBody @Validated PostMatchTagsByDomainReq body) {
        Operator operator = buildOperator(accountId, accountName);


        ImAccount imAccount = imAccountService.getById(oemId, tenantId, id);
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        List<MatchTag> matchTags = matchTagService.listByIds(oemId, tenantId, body.matchTagIds());
        for (MatchTag matchTag : matchTags) {
            matchTagImAccountService.createIfAbsent(matchTag, imAccount, operator);
        }

        return Resp.success();
    }
}
