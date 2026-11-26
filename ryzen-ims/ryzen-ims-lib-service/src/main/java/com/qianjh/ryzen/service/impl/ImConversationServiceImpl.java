package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.SchedulerToAppReportMessage;
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
    public ImConversation create(ImAccount imAccount, String code, String title, String subTitle, String avatar) {
        ImConversation entity = ImConversation.builder()
                .oemId(imAccount.getOemId())
                .tenantId(imAccount.getTenantId())
                .imAccountId(imAccount.getId())
                .code(code)

                // 收到消息更新
                .title(title)
                .subTitle(subTitle)
                .avatar(avatar)

                // 收到消息更新
                .lastMessageId(null)
                .lastMessageContent(null)
                .lastMessageTime(null)

                // 分配客服后更新
                .serviceAccountId(null)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImConversation createIfAbsent(ImAccount imAccount, String code, String title, String subTitle, String avatar) {
        ImConversation entity = getByUk(imAccount.getOemId(), imAccount.getTenantId(), imAccount.getId(), code);
        if (entity != null) {
            return entity;
        }
        return create(imAccount, code, title, subTitle, avatar);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean recordLastMessage(ImConversation entity, ImMessage message, SchedulerToAppReportMessage.Body schedulerMessage) {
        return lambdaUpdate()
                // info
                .set(StringUtils.isNotBlank(schedulerMessage.conversationTitle()), ImConversation::getTitle, schedulerMessage.conversationTitle())
                .set(StringUtils.isNotBlank(schedulerMessage.conversationSubTitle()), ImConversation::getSubTitle, schedulerMessage.conversationSubTitle())
                .set(StringUtils.isNotBlank(schedulerMessage.conversationAvatar()), ImConversation::getAvatar, schedulerMessage.conversationAvatar())
                // lastMessage
                .set(ImConversation::getLastMessageId, message.getId())
                .set(ImConversation::getLastMessageContent, message.getContent())
                .set(ImConversation::getLastMessageTime, message.getSentTime())
                // where
                .eq(ImConversation::getId, entity.getId())
                .eq(ImConversation::getOemId, entity.getOemId())
                .eq(ImConversation::getTenantId, entity.getTenantId())
                //
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateServiceAccount(ImConversation entity, ServiceAccount serviceAccount) {
        return lambdaUpdate()
                .set(ImConversation::getServiceAccountId, serviceAccount.getId())
                //
                .eq(ImConversation::getId, entity.getId())
                .eq(ImConversation::getOemId, entity.getOemId())
                .eq(ImConversation::getTenantId, entity.getTenantId())
                //
                .update();
    }
}
