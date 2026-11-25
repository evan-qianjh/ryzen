package com.qianjh.ryzen.controller.tenant.app;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetConversationsResp;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagImAccount;
import com.qianjh.ryzen.framework.common.dto.CursorPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.ims.enums.MatchTagStatus;
import com.qianjh.ryzen.mapper.ImConversationMapper;
import com.qianjh.ryzen.mapper.MatchTagImAccountMapper;
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
import java.util.stream.Collectors;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "对话")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Conversation_TenantAppController extends _TenantAppController {

    private final ImConversationMapper imConversationMapper;
    private final MatchTagImAccountMapper matchTagImAccountMapper;

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
                        .name(e.getName())
                        .avatar(e.getAvatar())
                        .lastMessageContent(e.getLastMessageContent())
                        .matchTags(
                                imAccountMatchTagImAccountsMap.getOrDefault(e.getImAccountId(), Collections.emptyList())
                                        .stream().map(GetMatchTagImAccountViews::getTag).toList()
                        )
                        .build()
                ).toList();

        CursorPage<GetConversationsResp> result = PageUtils.wrap(records, pageSize);
        return Resp.successOf(result);
    }
}
