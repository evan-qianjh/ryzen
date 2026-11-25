package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.ims.stream.domain.im_message.event.ImMessageCreatedEvent;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.ImMessageService;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushInsertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImMessageCreatedEvent_SubscriberImpl implements ImMessageCreatedEvent.Subscriber {

    private final ImMessageService imMessageService;
    private final ImConversationService imConversationService;
    private final PushInsertMessage.Publisher pushInsertMessagePublisher;

    @Override
    public void subscribe(ImMessageCreatedEvent.Body body) {
        log.debug("消费 ImMessageCreatedEvent ::: {}", body);

        ImMessage imMessage = imMessageService.getById(body.getOemId(), body.getTenantId(), body.getId());
        if (imMessage == null) {
            log.error("ImMessage不存在 ::: {}", body);
            return;
        }

        ImConversation imConversation = imConversationService.getById(imMessage.getOemId(), imMessage.getTenantId(), imMessage.getConversationId());
        if (imConversation == null) {
            log.error("ImConversation不存在 ::: {}", body);
            return;
        }

        // build conversation
        PushInsertMessage.Body.Conversation conversation = PushInsertMessage.Body.Conversation.builder()
                .id(imConversation.getId())
                .name(imConversation.getName())
                .avatar(imConversation.getAvatar())
                .serviceAccountId(imConversation.getServiceAccountId())
                .build();

        // build message
        PushInsertMessage.Body message = PushInsertMessage.Body.builder()
                .oemId(imMessage.getOemId())
                .tenantId(imMessage.getOemId())
                .id(imMessage.getId())
                .direction(imMessage.getDirection().name())
                .senderName(imMessage.getSenderName())
                .senderAvatar(imMessage.getSenderAvatar())
                .content(imMessage.getContent())
                .conversation(conversation)
                .build();
        pushInsertMessagePublisher.publish(message);
    }
}
