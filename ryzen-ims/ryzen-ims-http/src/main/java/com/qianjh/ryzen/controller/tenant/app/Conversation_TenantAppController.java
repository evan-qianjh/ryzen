package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetConversationsResp;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.framework.common.dto.CursorPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.service.ImConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "对话")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Conversation_TenantAppController extends _TenantAppController {

    private final ImConversationService conversationService;

    @Operation(summary = "分页查询对话")
    @GetMapping("/conversations")
    public Resp<CursorPage<GetConversationsResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                       @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                       @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                       //
                                                       @RequestParam(required = false) Long fromId,
                                                       @RequestParam(required = false) Integer limit) {
        int pageSize = safePageSize(limit);
        List<ImConversation> entities = conversationService.list(new LambdaQueryWrapper<ImConversation>()
                .eq(ImConversation::getOemId, oemId)
                .eq(ImConversation::getTenantId, tenantId)
                .eq(ImConversation::getReceptionAccountId, accountId)
                //
                .lt(fromId != null, ImConversation::getLastMessageId, fromId)
                .orderByDesc(ImConversation::getLastMessageId)
                .last("limit " + pageSize)
        );

        List<GetConversationsResp> records = entities.stream()
                .map(e -> GetConversationsResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .name(e.getName())
                        .avatar(e.getAvatar())
                        .lastMessageContent(e.getLastMessageContent())
                        .build()
                ).toList();

        CursorPage<GetConversationsResp> result = PageUtils.wrap(records, pageSize);
        return Resp.successOf(result);
    }
}
