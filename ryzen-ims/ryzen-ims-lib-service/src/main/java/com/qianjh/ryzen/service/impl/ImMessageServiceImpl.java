package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.mapper.ImMessageMapper;
import com.qianjh.ryzen.model.SchedulerMessage;
import com.qianjh.ryzen.service.ImMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ImMessageServiceImpl extends ServiceImpl<ImMessageMapper, ImMessage> implements ImMessageService {

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
    public ImMessage create(ImAccount imAccount, ImConversation conversation, SchedulerMessage schedulerMessage) {
        String messageCode = schedulerMessage.messageCode();
        ImMessage entity = ImMessage.builder()
                .oemId(imAccount.getOemId())
                .tenantId(imAccount.getTenantId())
                .conversationId(conversation.getId())
                .messageCode(messageCode)
                .messageSeq(IdUtils.ofString(messageCode))
                .direction(schedulerMessage.direction())
                .senderReceptionAccountId(null)
                .senderName(schedulerMessage.senderName())
                .senderAvatar(schedulerMessage.senderAvatar())
                .sentTime(DateTimeUtils.parseMillis(schedulerMessage.sentTime()))
                .content(schedulerMessage.content())
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImMessage createIfAbsent(ImAccount imAccount, ImConversation conversation, SchedulerMessage schedulerMessage) {
        ImMessage entity = getByUk(imAccount.getOemId(), imAccount.getTenantId(), conversation.getId(), schedulerMessage.messageCode());
        if (entity != null) {
            return entity;
        }
        return create(imAccount, conversation, schedulerMessage);
    }
}
