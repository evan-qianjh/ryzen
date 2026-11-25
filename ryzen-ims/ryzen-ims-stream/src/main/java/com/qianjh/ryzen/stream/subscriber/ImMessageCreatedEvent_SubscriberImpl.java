package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.ims.stream.domain.im_message.type.ImMessageCreatedEvent;
import com.qianjh.ryzen.service.*;
import com.qianjh.ryzen.service.dto.MatchTagImAccountWithMatchTagView;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushMessageInsert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImMessageCreatedEvent_SubscriberImpl implements ImMessageCreatedEvent.Subscriber {

    private final ImMessageService imMessageService;
    private final ImAccountService imAccountService;
    private final ImConversationService imConversationService;
    private final MatchTagImAccountService matchTagImAccountService;
    private final PushMessageInsert.Publisher pushMessageInsertPublisher;
    private final ImConversationDispatchService imConversationDispatchService;

    @Override
    public void subscribe(ImMessageCreatedEvent.Body body) {
        log.debug("消费 ImMessageCreatedEvent ::: {}", body);
        Long oemId = body.getOemId();
        Long tenantId = body.getTenantId();

        ImMessage imMessage = imMessageService.getById(oemId, tenantId, body.getId());
        if (imMessage == null) {
            log.error("ImMessage不存在 ::: {}", body);
            return;
        }

        ImConversation imConversation = imConversationService.getById(oemId, tenantId, imMessage.getConversationId());
        if (imConversation == null) {
            log.error("ImConversation不存在 ::: {}", body);
            return;
        }

        ImAccount imAccount = imAccountService.getById(oemId, tenantId, imConversation.getImAccountId());
        if (imAccount == null) {
            log.error("ImAccount不存在 ::: {}", body);
            return;
        }

        // 调度服务账号
        ServiceAccount serviceAccount = imConversationDispatchService.dispatch(imConversation);
        if (serviceAccount == null) {
            log.warn("未调度到服务账户 ::: imConversation={}", imConversation);
            return;
        }

        // 对话标签
        List<String> matchTags = matchTagImAccountService.getByImAccountWithMatchTag(oemId, tenantId, imAccount.getId())
                .stream().map(MatchTagImAccountWithMatchTagView::getTag).toList();

        // build conversation
        PushMessageInsert.Body.Conversation conversation = PushMessageInsert.Body.Conversation.builder()
                .id(imConversation.getId())
                .imType(imAccount.getImType().name())
                .name(imConversation.getName())
                .avatar(imConversation.getAvatar())
                .matchTags(matchTags)
                .serviceAccountId(serviceAccount.getId())
                .build();

        // build message
        PushMessageInsert.Body message = PushMessageInsert.Body.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .id(imMessage.getId())
                .seq(imMessage.getMessageSeq())
                .direction(imMessage.getDirection().name())
                .senderName(imMessage.getSenderName())
                .senderAvatar(imMessage.getSenderAvatar())
                .content(imMessage.getContent())
                .sentTime(DateTimeUtils.getTime(imMessage.getSentTime()))
                .conversation(conversation)
                .build();
        pushMessageInsertPublisher.publish(message);
    }
}
