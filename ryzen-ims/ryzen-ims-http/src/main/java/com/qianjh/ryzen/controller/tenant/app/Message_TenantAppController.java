package com.qianjh.ryzen.controller.tenant.app;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.qianjh.ryzen.controller.tenant.app.dto.GetMessagesResp;
import com.qianjh.ryzen.controller.tenant.app.dto.PostMessageReq;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.framework.common.dto.CursorPage;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderTenant;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.http.controller.tenant._TenantAppController;
import com.qianjh.ryzen.framework.http.model.Resp;
import com.qianjh.ryzen.framework.http.model.RespMc;
import com.qianjh.ryzen.framework.http.webmvc.util.McUtils;
import com.qianjh.ryzen.framework.http.webmvc.util.PageUtils;
import com.qianjh.ryzen.framework.stream.util.StreamUtils;
import com.qianjh.ryzen.ims.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.ims.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.ims.model.SchedulerTaskRequest;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.ImMessageService;
import com.qianjh.ryzen.stream.ImsSchedulerTaskRequestStreamProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
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

    private final ImMessageService messageService;
    private final ImAccountService imAccountService;
    private final ImConversationService conversationService;
    private final ImsSchedulerTaskRequestStreamProducer imsSchedulerRequestTopicStreamProducer;

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
        List<ImMessage> entities = messageService.list(new LambdaQueryWrapper<ImMessage>()
                .eq(ImMessage::getOemId, oemId)
                .eq(ImMessage::getTenantId, tenantId)
                .eq(ImMessage::getConversationId, conversationId)
                //
                .lt(fromId != null, ImMessage::getMessageSeq, fromId)
                .orderByDesc(ImMessage::getMessageSeq)
                .last("limit " + pageSize)
        );

        List<GetMessagesResp> records = entities.stream()
                .map(e -> GetMessagesResp.builder()
                        .id(IdUtils.toString(e.getId()))
                        .seq(IdUtils.toString(e.getMessageSeq()))
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
        ImConversation conversation = conversationService.getById(body.conversationId());
        Assert.notNull(conversation, McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));

        ImAccount imAccount = imAccountService.getById(conversation.getImAccountId());
        Assert.notNull(imAccount, McUtils.i18n(RespMc.ILLEGAL_ARGUMENT));

        //
        if (imAccount.getAssignStatus() != ImAccountAssignStatus.ASSIGNED || imAccount.getExecutorAssignmentId() == null) {
            return Resp.failure("请先分配执行器");
        }
        if (imAccount.getExecutorStatus() != ImAccountExecutorStatus.STARTED) {
            return Resp.failure("请先启动执行器");
        }

        // 生成一个随机码+内容发给调度器
        SchedulerTaskRequest message = SchedulerTaskRequest.builder()
                .code(IdUtils.generateUUID()) // TODO
                .assignmentId(imAccount.getExecutorAssignmentId())
                .conversationCode(conversation.getCode())
                .content(body.content())
                .build();
        imsSchedulerRequestTopicStreamProducer.send(
                MessageBuilder.withPayload(StreamUtils.toJson(message)).build()
        );

        // TODO 通过ws推送一个发送中的消息给客户端
        // TODO 等待调度器执行结果反馈，将结果通过ws推送给客户端

        return Resp.success();
    }

}
