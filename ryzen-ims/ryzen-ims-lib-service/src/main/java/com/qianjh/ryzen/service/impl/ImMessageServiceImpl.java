package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageCreated;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.SchedulerToAppReportMessage;
import com.qianjh.ryzen.mapper.ImMessageMapper;
import com.qianjh.ryzen.service.ImMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImMessageServiceImpl extends ServiceImpl<ImMessageMapper, ImMessage> implements ImMessageService {

    private final ImMessageCreated.Publisher imMessageCreatedEventPublisher;

    @Override
    public ImMessage getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<ImMessage>()
                .eq(ImMessage::getOemId, oemId)
                .eq(ImMessage::getTenantId, tenantId)
                .eq(ImMessage::getId, id)
        );
    }

    @Override
    public ImMessage getByUk(Long oemId, Long tenantId, Long conversationId, String code) {
        return getOne(new LambdaQueryWrapper<ImMessage>()
                .eq(ImMessage::getOemId, oemId)
                .eq(ImMessage::getTenantId, tenantId)
                .eq(ImMessage::getConversationId, conversationId)
                .eq(ImMessage::getMessageCode, code)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImMessage create(ImAccount imAccount, ImConversation conversation, SchedulerToAppReportMessage.Body message) {
        String messageCode = message.messageCode();
        ImMessage entity = ImMessage.builder()
                .oemId(imAccount.getOemId())
                .tenantId(imAccount.getTenantId())
                .conversationId(conversation.getId())
                .messageCode(messageCode)
                .messageSeq(IdUtils.ofString(messageCode))
                .direction(message.direction())
                .senderServiceAccountId(null)
                .senderName(message.senderName())
                .senderAvatar(message.senderAvatar())
                .sentTime(DateTimeUtils.parseMillis(message.sentTime()))
                .content(message.content())
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);

        imMessageCreatedEventPublisher.publish(ImMessageCreated.Body.builder()
                .id(entity.getId())
                .oemId(entity.getOemId())
                .tenantId(entity.getTenantId())
                .build());
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImMessage createIfAbsent(ImAccount imAccount, ImConversation conversation, SchedulerToAppReportMessage.Body message) {
        ImMessage entity = getByUk(imAccount.getOemId(), imAccount.getTenantId(), conversation.getId(), message.messageCode());
        if (entity != null) {
            return entity;
        }
        return create(imAccount, conversation, message);
    }
}
