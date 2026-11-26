package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetAvailableTransferServiceAccountsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.GetServiceAccountsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostMatchTagsByDomainReq;
import com.qianjh.ryzen.controller.tenant.app.dto.PutServiceAccountOnlineReq;
import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.framework.common.dto.OffsetPage;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.framework.common.dto.Receipt;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.ims.enums.ServiceAccountStatus;
import com.qianjh.ryzen.mapper.ServiceAccountMapper;
import com.qianjh.ryzen.service.*;
import com.qianjh.ryzen.service.dto.MatchTagServiceAccountWithMatchTagView;
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
import java.util.Map;
import java.util.Objects;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "服务账号")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ServiceAccount_TenantAppController extends _TenantAppController {

    private final MatchTagService matchTagService;
    private final ImAccountService imAccountService;
    private final ServiceAccountMapper serviceAccountMapper;
    private final ServiceAccountService serviceAccountService;
    private final ImConversationService imConversationService;
    private final MatchTagServiceAccountService matchTagServiceAccountService;

    @Operation(summary = "查询可转接列表")
    @GetMapping("/available-transfer-service-accounts")
    public Resp<List<GetAvailableTransferServiceAccountsResp>> availableTransfers(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                                                  @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                                                  @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                                                  Long conversationId) {
        // conversation
        ImConversation conversation = imConversationService.getById(oemId, tenantId, conversationId);
        if (conversation == null) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }
        // imAccount
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, conversation.getImAccountId());
        if (imAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.SYSTEM_EXCEPTION));
        }
        List<ServiceAccount> entities = serviceAccountMapper.selectJoinList(new MPJLambdaWrapper<ServiceAccount>()
                        .innerJoin(ServiceAccountImType.class, (join) -> join
                                .eq(ServiceAccountImType::getServiceAccountId, ServiceAccount::getId)
                                .eq(ServiceAccountImType::getImType, imAccount.getImType())
                        )
                        .eq(ServiceAccount::getOemId, oemId)
                        .eq(ServiceAccount::getTenantId, tenantId)
                        .eq(ServiceAccount::getOnline, true)
                )
                .stream()
                // 过滤当前接待
                .filter(e -> !Objects.equals(conversation.getServiceAccountId(), e.getAccountId()))
                .toList();
        if (entities.isEmpty()) {
            return Resp.successOf(Collections.emptyList());
        }

        List<Long> serviceAccountIds = entities.stream().map(ServiceAccount::getId).toList();
        Map<Long, List<MatchTagServiceAccountWithMatchTagView>> serviceAccountMatchTagsMap = matchTagServiceAccountService.groupByServiceAccountsWithMatchTag(oemId, tenantId, serviceAccountIds);

        List<GetAvailableTransferServiceAccountsResp> result = entities.stream()
                .map(e -> GetAvailableTransferServiceAccountsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .name(e.getAccountName())
                        .matchTags(
                                serviceAccountMatchTagsMap.getOrDefault(e.getId(), Collections.emptyList())
                                        .stream().map(MatchTagServiceAccountWithMatchTagView::getTag).toList()
                        )
                        .build()
                ).toList();

        return Resp.successOf(result);
    }

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
    public Resp<Receipt> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                                @RequestBody @Validated TenantPostServiceAccountReq body) {
        Operator operator = buildOperator(accountId, accountName);

        ServiceAccount entity = serviceAccountService.getByUk(oemId, tenantId, body.accountId());
        if (entity != null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_ALREADY_EXIST));
        }
        entity = serviceAccountService.create(oemId, tenantId, body, operator);

        // 如果指定了标签，则直接分配标签
        if (body.matchTagIds() != null && !body.matchTagIds().isEmpty()) {
            List<MatchTag> matchTags = matchTagService.listByIds(oemId, tenantId, body.matchTagIds());

            for (MatchTag matchTag : matchTags) {
                matchTagServiceAccountService.createIfAbsent(matchTag, entity, operator);
            }
        }

        return Resp.successOf(Receipt.build(entity.getId()));
    }

    @Operation(summary = "修改")
    @PatchMapping("/service-account/{id}")
    public Resp<?> patch(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                         @PathVariable Long id,
                         @RequestBody @Validated TenantPatchServiceAccountReq body) {
        Operator operator = buildOperator(accountId, accountName);

        ServiceAccount entity = serviceAccountService.getById(oemId, tenantId, id);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }
        boolean success = serviceAccountService.patch(entity, body, operator);
        return success ? Resp.success() : Resp.failure();
    }

    @Operation(summary = "创建匹配标签")
    @PostMapping("/service-account/{id}/match-tags")
    public Resp<?> createMatchTags(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                   @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                   @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                   @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                                   @PathVariable Long id,
                                   @RequestBody @Validated PostMatchTagsByDomainReq body) {
        Operator operator = buildOperator(accountId, accountName);

        ServiceAccount serviceAccount = serviceAccountService.getById(oemId, tenantId, id);
        if (serviceAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        List<MatchTag> matchTags = matchTagService.listByIds(oemId, tenantId, body.matchTagIds());

        for (MatchTag matchTag : matchTags) {
            matchTagServiceAccountService.createIfAbsent(matchTag, serviceAccount, operator);
        }

        return Resp.success();
    }

    @Operation(summary = "上/下线(使用者视角)")
    @PutMapping("/service-account/online")
    public Resp<?> putOnline(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                             @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                             @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                             @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                             @RequestBody @Validated PutServiceAccountOnlineReq body) {
        Operator operator = buildOperator(accountId, accountName);
        ServiceAccount entity = serviceAccountService.getByUk(oemId, tenantId, accountId);
        if (entity == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        TenantPatchServiceAccountReq patchBody = TenantPatchServiceAccountReq.builder()
                .online(body.online())
                .build();
        boolean success = serviceAccountService.patch(entity, patchBody, operator);
        return success ? Resp.success() : Resp.failure();
    }
}
