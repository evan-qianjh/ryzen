package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.controller.tenant.app.dto.GetMessagesResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostMessageReq;
import com.qianjh.ryzen.entity.Message;
import com.qianjh.ryzen.framework.common.dto.CursorPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController.PATH_PREFIX;

@Slf4j
@Tag(name = "消息")
@RestController
@RequestMapping(PATH_PREFIX)
@RequiredArgsConstructor
public class Message_TenantAppController extends _TenantAppController {

    private final MessageService messageService;

    @Operation(summary = "分页查询对话")
    @GetMapping("/messages")
    public Resp<CursorPage<GetMessagesResp>> page(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                                                  @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                                                  @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                                                  //
                                                  Long conversationId,
                                                  @RequestParam(required = false) Long fromId,
                                                  @RequestParam(required = false) Integer limit) {
        int pageSize = safePageSize(limit);
        List<Message> entities = messageService.list(new LambdaQueryWrapper<Message>()
                .eq(Message::getOemId, oemId)
                .eq(Message::getTenantId, tenantId)
                .eq(Message::getConversationId, conversationId)
                //
                .lt(fromId != null, Message::getMessageSeq, fromId)
                .orderByDesc(Message::getMessageSeq)
                .last("limit " + pageSize)
        );

        List<GetMessagesResp> records = entities.stream()
                .map(e -> GetMessagesResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .direction(e.getDirection())
                        .senderName(e.getSenderName())
                        .senderAvatar(e.getSenderAvatar())
                        .sentTime(DateTimeUtils.getTime(e.getSentTime()))
                        .content(e.getContent())
                        .build()
                ).toList();

        CursorPage<GetMessagesResp> result = PageUtils.wrap(records, pageSize);
        return Resp.successOf(result);
    }

    @Operation(summary = "创建消息")
    @PostMapping("/message")
    public Resp<?> create(@RequestHeader(GatewayHeaderTenant.OEM_ID) Long oemId,
                          @RequestHeader(GatewayHeaderTenant.TENANT_ID) Long tenantId,
                          @RequestHeader(GatewayHeaderTenant.ACCOUNT_ID) Long accountId,
                          @RequestBody @Validated PostMessageReq body
    ) {
        // TODO 生成一个随机码+内容发给调度器
        // 通过ws推送一个发送中的消息给客户端
        // 等待调度器执行结果反馈，将结果通过ws推送给客户端
        return null;
    }
}
