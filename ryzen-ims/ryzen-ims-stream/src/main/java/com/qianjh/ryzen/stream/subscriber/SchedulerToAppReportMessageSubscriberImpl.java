package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.SchedulerToAppReportMessage;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.ImMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerToAppReportMessageSubscriberImpl implements SchedulerToAppReportMessage.Subscriber {

    private final ImAccountService imAccountService;
    private final ImMessageService imMessageService;
    private final ImConversationService imConversationService;

    @Override
    public void subscribe(SchedulerToAppReportMessage.Body body) {
        log.debug("消费 SchedulerToAppReportMessage ::: {}", body);

        // TODO saas oem/tenant
        ImAccount imAccount = imAccountService.getByUk(body.imType(), body.accountCode());
        if (imAccount == null) {
            log.error("未创建ImAccount ::: {}", body);
            return;
        }

        // 对话
        ImConversation conversation = imConversationService.createIfAbsent(
                imAccount,
                body.conversationCode(),
                body.conversationTitle(),
                body.conversationSubTitle(),
                body.conversationAvatar()
        );

        // 消息
        ImMessage messageEntity = imMessageService.createIfAbsent(imAccount, conversation, body);

        // 如果是来自回执消息，补充信息
        if (messageEntity.isFromReceipt()) {
            imMessageService.fillFromReceiptMissInfo(messageEntity, body);
        }

        imConversationService.recordLastMessage(conversation, messageEntity, body);
    }
}
