package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.SchedulerTask;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.ims.enums.MessageDirection;
import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageCreated;
import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageFillReceipt;
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
    private final ImMessageFillReceipt.Publisher imMessageFillReceiptPublisher;

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
                .fromReceipt(false)
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImMessage createByReceipt(SchedulerTask task, String imReceiptCode) {
        ImMessage entity = ImMessage.builder()
                .id(task.getId()) // 必须如此，与ws发送预览id对应
                .oemId(task.getOemId())
                .tenantId(task.getTenantId())
                .conversationId(task.getConversationId())
                .messageCode(imReceiptCode)
                .messageSeq(IdUtils.ofString(imReceiptCode))
                .direction(MessageDirection.OUTBOUND)
                .senderServiceAccountId(task.getServiceAccountId())

                .senderName(null)
                .senderAvatar(null)
                .sentTime(null)

                .content(task.getContent())
                .fromReceipt(true)
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
    public ImMessage createByReceiptIfAbsent(SchedulerTask task, String imReceiptCode) {
        ImMessage entity = getByUk(task.getOemId(), task.getTenantId(), task.getConversationId(), imReceiptCode);
        if (entity != null) {
            return entity;
        }
        return createByReceipt(task, imReceiptCode);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void fillFromReceiptMissInfo(ImMessage entity, SchedulerToAppReportMessage.Body body) {
        lambdaUpdate()
                .set(ImMessage::getSenderName, body.senderName())
                .set(ImMessage::getSenderAvatar, body.senderAvatar())
                .set(ImMessage::getSentTime, DateTimeUtils.parseMillis(body.sentTime()))
                //
                .eq(ImMessage::getId, entity.getId())
                .eq(ImMessage::getOemId, entity.getOemId())
                .eq(ImMessage::getTenantId, entity.getTenantId())
                .update();

        // 发布事件
        imMessageFillReceiptPublisher.publish(ImMessageFillReceipt.Body.builder()
                .id(entity.getId())
                .oemId(entity.getOemId())
                .tenantId(entity.getTenantId())
                .build()
        );
    }
}
