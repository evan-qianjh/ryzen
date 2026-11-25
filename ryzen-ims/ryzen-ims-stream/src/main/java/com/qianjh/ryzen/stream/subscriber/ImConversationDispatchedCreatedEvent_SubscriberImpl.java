package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.ims.stream.domain.im_conversation_dispatched.type.ImConversationDispatchedCreatedEvent;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.dto.MatchTagImAccountWithMatchTagView;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation.type.PushConversationDispatch;
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
public class ImConversationDispatchedCreatedEvent_SubscriberImpl implements ImConversationDispatchedCreatedEvent.Subscriber {

    private final ImConversationService imConversationService;
    private final ImAccountService imAccountService;
    private final MatchTagImAccountService matchTagImAccountService;
    private final PushConversationDispatch.Publisher pushConversationDispatchPublisher;

    @Override
    public void subscribe(ImConversationDispatchedCreatedEvent.Body body) {
        log.debug("消费 ImConversationDispatchedCreatedEvent ::: {}", body);

        Long oemId = body.getOemId();
        Long tenantId = body.getTenantId();
        Long imConversationId = body.getImConversationId();

        ImConversation conversation = imConversationService.getById(oemId, tenantId, imConversationId);
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, conversation.getImAccountId());
        List<MatchTagImAccountWithMatchTagView> matchTagImAccountWithMatchTagViews = matchTagImAccountService.getByImAccountWithMatchTag(oemId, tenantId, imAccount.getId());
        List<String> matchTags = matchTagImAccountWithMatchTagViews.stream().map(MatchTagImAccountWithMatchTagView::getTag).toList();

        // 通知tenant-ws发消息
        PushConversationDispatch.Body message = PushConversationDispatch.Body.builder()
                .id(imConversationId)
                .oemId(oemId)
                .tenantId(tenantId)
                .imType(imAccount.getImType().name())
                .name(conversation.getName())
                .avatar(conversation.getAvatar())
                .lastMessageContent(conversation.getLastMessageContent())
                .lastMessageId(conversation.getLastMessageId())
                .matchTags(matchTags)
                .serviceAccountId(body.getServiceAccountId())
                .beforeServiceAccountId(body.getBeforeServiceAccountId())
                .build();
        pushConversationDispatchPublisher.publish(message);
    }
}
