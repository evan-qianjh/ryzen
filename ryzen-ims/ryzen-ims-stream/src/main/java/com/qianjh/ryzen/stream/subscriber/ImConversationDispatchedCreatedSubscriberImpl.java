package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.ims.enums.MessageContentType;
import com.qianjh.ryzen.ims.stream.im_conversation.im_conversation_dispatched.ImConversationDispatchedCreated;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.dto.MatchTagImAccountWithMatchTagView;
import com.qianjh.ryzen.tenant.stream.from_ims.push_conversation.PushConversationDispatched;
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
public class ImConversationDispatchedCreatedSubscriberImpl implements ImConversationDispatchedCreated.Subscriber {

    private final ImConversationService imConversationService;
    private final ImAccountService imAccountService;
    private final MatchTagImAccountService matchTagImAccountService;
    private final PushConversationDispatched.Publisher pushConversationDispatchPublisher;

    @Override
    public void subscribe(ImConversationDispatchedCreated.Body body) {
        log.debug("消费 ImConversationDispatchedCreatedEvent ::: {}", body);

        Long oemId = body.getOemId();
        Long tenantId = body.getTenantId();
        Long imConversationId = body.getImConversationId();

        ImConversation conversation = imConversationService.getById(oemId, tenantId, imConversationId);
        ImAccount imAccount = imAccountService.getById(oemId, tenantId, conversation.getImAccountId());
        List<MatchTagImAccountWithMatchTagView> matchTagImAccountWithMatchTagViews = matchTagImAccountService.getByImAccountWithMatchTag(oemId, tenantId, imAccount.getId());
        List<String> matchTags = matchTagImAccountWithMatchTagViews.stream().map(MatchTagImAccountWithMatchTagView::getTag).toList();

        MessageContentType contentType = conversation.getLastMessageContentType();

        // 通知tenant-ws发消息
        PushConversationDispatched.Body message = PushConversationDispatched.Body.builder()
                .id(imConversationId)
                .oemId(oemId)
                .tenantId(tenantId)
                .imType(imAccount.getImType().name())
                .title(conversation.getTitle())
                .subTitle(conversation.getSubTitle())
                .avatar(conversation.getAvatar())
                .lastMessageContent(conversation.getLastMessageContent())
                .lastMessageContentType(contentType == null ? null : contentType.name())
                .lastMessageId(conversation.getLastMessageId())
                .lastMessageTime(DateTimeUtils.getTime(conversation.getLastMessageTime()))
                .matchTags(matchTags)
                .serviceAccountId(body.getServiceAccountId())
                .beforeServiceAccountId(body.getBeforeServiceAccountId())
                .build();
        pushConversationDispatchPublisher.publish(message);
    }
}
