package com.qianjh.ryzen.controller.tenant.app;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetConversationsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.GetTransferServiceAccountsResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostConversationTransferReq;
import com.qianjh.ryzen.entity.*;
import com.qianjh.ryzen.framework.common.dto.CursorPage;
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
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.MatchTagStatus;
import com.qianjh.ryzen.ims.enums.ServiceAccountStatus;
import com.qianjh.ryzen.mapper.ImConversationMapper;
import com.qianjh.ryzen.mapper.MatchTagImAccountMapper;
import com.qianjh.ryzen.mapper.ServiceAccountMapper;
import com.qianjh.ryzen.service.*;
import com.qianjh.ryzen.service.dto.MatchTagServiceAccountWithMatchTagView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "对话")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class ImConversation_TenantAppController extends _TenantAppController {

    private final ImConversationMapper imConversationMapper;
    private final MatchTagImAccountMapper matchTagImAccountMapper;
    private final ImConversationService imConversationService;
    private final ImConversationDispatchService imConversationDispatchService;
    private final ServiceAccountService serviceAccountService;
    private final ImAccountService imAccountService;
    private final ServiceAccountMapper serviceAccountMapper;
    private final MatchTagServiceAccountService matchTagServiceAccountService;

    @Data
    @EqualsAndHashCode(callSuper = true)
    static class GetImConversationViews extends ImConversation {
        private ImType imType;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    static class GetMatchTagImAccountViews extends MatchTagImAccount {
        private String tag;
    }

    @Operation(summary = "分页查询对话")
    @GetMapping("/conversations")
    public Resp<CursorPage<GetConversationsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                       @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                       @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                       //
                                                       @RequestParam(required = false) Long fromId,
                                                       @RequestParam(required = false) Integer limit) {
        int pageSize = safePageSize(limit);
        List<GetImConversationViews> entities = imConversationMapper.selectJoinList(GetImConversationViews.class, new MPJLambdaWrapper<ImConversation>()
                //
                .selectAll(ImConversation.class)
                .selectAs(ImAccount::getImType, GetImConversationViews::getImType)
                //
                .leftJoin(ImAccount.class, ImAccount::getId, ImConversation::getImAccountId)
                //
                .eq(ImConversation::getOemId, oemId)
                .eq(ImConversation::getTenantId, tenantId)
                .eq(ImConversation::getServiceAccountId, accountId)
                //
                .lt(fromId != null, ImConversation::getLastMessageId, fromId)
                .orderByDesc(ImConversation::getLastMessageId)
                .last("limit " + pageSize)
        );

        if (entities.isEmpty()) {
            return Resp.successOf(PageUtils.wrap(Collections.emptyList(), pageSize));
        }

        // 获取IM账户标签
        List<Long> imAccountIds = entities.stream().map(GetImConversationViews::getImAccountId).toList();
        Map<Long, List<GetMatchTagImAccountViews>> imAccountMatchTagImAccountsMap = matchTagImAccountMapper.selectJoinList(GetMatchTagImAccountViews.class, new MPJLambdaWrapper<MatchTagImAccount>()
                .selectAll(MatchTagImAccount.class)
                .selectAs(MatchTag::getTag, GetMatchTagImAccountViews::getTag)
                //
                .leftJoin(MatchTag.class, MatchTag::getId, MatchTagImAccount::getMatchTagId)
                //
                .eq(MatchTagImAccount::getOemId, oemId)
                .eq(MatchTagImAccount::getTenantId, tenantId)
                .in(MatchTagImAccount::getImAccountId, imAccountIds)
                .eq(MatchTag::getStatus, MatchTagStatus.ENABLED)
        ).stream().collect(Collectors.groupingBy(GetMatchTagImAccountViews::getImAccountId));

        //
        List<GetConversationsResp> records = entities.stream()
                .map(e -> GetConversationsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .imType(e.imType)
                        .title(e.getTitle())
                        .subTitle(e.getSubTitle())
                        .avatar(e.getAvatar())
                        .lastMessageContent(e.getLastMessageContent())
                        .lastMessageId(IdUtils.toString(e.getLastMessageId()))
                        .lastMessageTime(DateTimeUtils.getTime(e.getLastMessageTime()))
                        .matchTags(
                                imAccountMatchTagImAccountsMap.getOrDefault(e.getImAccountId(), Collections.emptyList())
                                        .stream().map(GetMatchTagImAccountViews::getTag).toList()
                        )
                        .build()
                ).toList();

        CursorPage<GetConversationsResp> result = PageUtils.wrap(records, pageSize);
        return Resp.successOf(result);
    }

    @Operation(summary = "查询转接服务账户")
    @GetMapping("/conversation/{id}/transfer-service-accounts")
    public Resp<List<GetTransferServiceAccountsResp>> availableTransfers(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                                         @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                                         @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                                         @PathVariable Long id) {
        // conversation
        ImConversation conversation = imConversationService.getById(oemId, tenantId, id);
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
                )
                .stream()
                // 过滤当前接待
                .filter(e -> !Objects.equals(conversation.getServiceAccountId(), e.getAccountId()))
                // 在线的靠前展示
                .sorted((o1, o2) -> o2.getOnline().compareTo(o1.getOnline()))
                .toList();
        if (entities.isEmpty()) {
            return Resp.successOf(Collections.emptyList());
        }

        List<Long> serviceAccountIds = entities.stream().map(ServiceAccount::getId).toList();
        Map<Long, List<MatchTagServiceAccountWithMatchTagView>> serviceAccountMatchTagsMap =
                matchTagServiceAccountService.groupByServiceAccountsWithMatchTag(oemId, tenantId, serviceAccountIds);

        List<GetTransferServiceAccountsResp> result = entities.stream()
                .map(e -> GetTransferServiceAccountsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .name(e.getAccountName())
                        .online(e.getOnline())
                        .matchTags(
                                serviceAccountMatchTagsMap.getOrDefault(e.getId(), Collections.emptyList())
                                        .stream().map(MatchTagServiceAccountWithMatchTagView::getTag).toList()
                        )
                        .build()
                ).toList();

        return Resp.successOf(result);
    }

    @Operation(summary = "转接")
    @PostMapping("/conversation/{id}/transfer")
    public Resp<Receipt> transfer(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                  @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                  @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                  @RequestHeader(GatewayHeaderTenant.ACCOUNT_NAME) String accountName,
                                  @PathVariable Long id,
                                  @RequestBody PostConversationTransferReq body) {
        Operator operator = buildOperator(accountId, accountName);

        // conversation
        ImConversation conversation = imConversationService.getById(oemId, tenantId, id);
        if (conversation == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        // check
        if (Objects.equals(conversation.getServiceAccountId(), body.serviceAccountId())) {
            return Resp.failure(McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));
        }

        // serviceAccount
        ServiceAccount serviceAccount = serviceAccountService.getById(oemId, tenantId, body.serviceAccountId());
        if (serviceAccount == null) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_EXIST));
        }

        // check
        if (serviceAccount.getStatus() != ServiceAccountStatus.ENABLED || !serviceAccount.getOnline()) {
            return Resp.failure(McUtils.i18n(RespMc.TARGET_NOT_AVAILABLE));
        }

        // 调度
        ImConversationDispatched record = imConversationDispatchService.dispatch(
                conversation, serviceAccount, ImConversationDispatchType.TRANSFER, operator
        );

        return Resp.successOf(Receipt.build(record.getId()));
    }
}
