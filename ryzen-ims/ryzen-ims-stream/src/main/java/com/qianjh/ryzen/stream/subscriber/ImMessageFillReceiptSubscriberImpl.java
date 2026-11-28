package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.ims.stream.im_message.im_message.ImMessageFillReceipt;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.ImMessageService;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.dto.MatchTagImAccountWithMatchTagView;
import com.qianjh.ryzen.tenant.stream.from_ims.push_message.PushMessageInsert;
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
public class ImMessageFillReceiptSubscriberImpl implements ImMessageFillReceipt.Subscriber {

    private final ImMessageService imMessageService;
    private final ImAccountService imAccountService;
    private final ImConversationService imConversationService;
    private final MatchTagImAccountService matchTagImAccountService;
    private final PushMessageInsert.Publisher pushMessageInsertPublisher;

    @Override
    public void subscribe(ImMessageFillReceipt.Body body) {
        log.debug("消费 ImMessageFillReceipt ::: {}", body);
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

        // 对话标签
        List<String> matchTags = matchTagImAccountService.getByImAccountWithMatchTag(oemId, tenantId, imAccount.getId())
                .stream().map(MatchTagImAccountWithMatchTagView::getTag).toList();

        // build conversation
        PushMessageInsert.Body.Conversation conversation = PushMessageInsert.Body.Conversation.builder()
                .id(imConversation.getId())
                .imType(imAccount.getImType().name())
                .title(imConversation.getTitle())
                .subTitle(imConversation.getSubTitle())
                .avatar(imConversation.getAvatar())
                .matchTags(matchTags)
                .serviceAccountId(imConversation.getServiceAccountId())
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
                .version(2L)
                .preview(false)
                .build();
        pushMessageInsertPublisher.publish(message);

        log.info("消息完善，推送ws消息-INSERT ::: messageId={}", imMessage.getId());
    }
}
