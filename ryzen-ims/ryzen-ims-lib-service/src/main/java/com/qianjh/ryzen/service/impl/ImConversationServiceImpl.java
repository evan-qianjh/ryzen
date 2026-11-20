package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.enums.MessageDirection;
import com.qianjh.ryzen.mapper.ImConversationMapper;
import com.qianjh.ryzen.service.ImConversationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImConversationServiceImpl extends ServiceImpl<ImConversationMapper, ImConversation> implements ImConversationService {
    @Override
    public ImConversation getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<ImConversation>()
                .eq(ImConversation::getOemId, oemId)
                .eq(ImConversation::getTenantId, tenantId)
                .eq(ImConversation::getId, id)
        );
    }

    @Override
    public ImConversation getByUk(Long oemId, Long tenantId, Long imAccountId, String code) {
        return getOne(new LambdaQueryWrapper<ImConversation>()
                .eq(ImConversation::getOemId, oemId)
                .eq(ImConversation::getTenantId, tenantId)
                .eq(ImConversation::getImAccountId, imAccountId)
                .eq(ImConversation::getCode, code)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImConversation create(ImAccount imAccount, String code) {
        ImConversation entity = ImConversation.builder()
                .oemId(imAccount.getOemId())
                .tenantId(imAccount.getTenantId())
                .imAccountId(imAccount.getId())
                .code(code)
                .name(null)
                .avatar(null)
                .receptionAccountId(null)
                .lastMessageId(null)
                .lastMessageContent(null)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImConversation createIfAbsent(ImAccount imAccount, String code) {
        ImConversation entity = getByUk(imAccount.getOemId(), imAccount.getTenantId(), imAccount.getId(), code);
        if (entity != null) {
            return entity;
        }
        return create(imAccount, code);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean recordLastMessage(ImConversation entity, ImMessage message) {
        return lambdaUpdate()
                .set(message.getDirection() == MessageDirection.INBOUND
                                && StringUtils.isNotBlank(message.getSenderName()),
                        ImConversation::getName, message.getSenderName()
                )
                .set(message.getDirection() == MessageDirection.INBOUND
                                && StringUtils.isNotBlank(message.getSenderAvatar()),
                        ImConversation::getAvatar, message.getSenderAvatar()
                )
                .set(ImConversation::getLastMessageId, message.getId())
                .set(ImConversation::getLastMessageContent, message.getContent())
                //
                .eq(ImConversation::getId, entity.getId())
                .eq(ImConversation::getOemId, entity.getOemId())
                .eq(ImConversation::getTenantId, entity.getTenantId())
                //
                .update();
    }
}
