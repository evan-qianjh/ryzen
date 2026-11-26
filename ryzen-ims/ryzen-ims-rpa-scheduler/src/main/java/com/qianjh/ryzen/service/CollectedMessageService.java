package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.SchedulerMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.ims.dto.ExecutorMessage;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.type.SchedulerToAppReportMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectedMessageService {
    private final SchedulerMessageService schedulerMessageService;
    private final SchedulerToAppReportMessage.Publisher schedulerToAppReportMessagePublisher;

    public void forward(ExecutorMessage event) {
        if (event == null || blank(event.executorCode()) || blank(event.accountCode())
                || blank(event.messageCode()) || blank(event.conversationCode())) {
            log.warn("Ignore invalid collected message: {}", event);
            return;
        }

        // 落地去重
        SchedulerMessage entity = schedulerMessageService.getByUk(event.imType(), event.messageCode());
        if (entity != null) {
            return;
        }

        SchedulerMessage schedulerMessage = schedulerMessageService.create(event);

        // 上报
        schedulerToAppReportMessagePublisher.publish(convert(schedulerMessage));
        log.debug("Collected message forwarded: messageCode={}, accountCode={}, conversationCode={}",
                event.messageCode(), event.accountCode(), event.conversationCode());

    }

    private SchedulerToAppReportMessage.Body convert(SchedulerMessage entity) {
        return SchedulerToAppReportMessage.Body.builder()
                .imType(entity.getImType())
                .accountCode(entity.getAccountCode())
                .direction(entity.getDirection())
                .messageCode(entity.getMessageCode())
                .conversationCode(entity.getConversationCode())
                .conversationTitle(entity.getConversationTitle())
                .conversationSubTitle(entity.getConversationSubTitle())
                .conversationAvatar(entity.getConversationAvatar())
                .senderCode(entity.getSenderCode())
                .senderName(entity.getSenderName())
                .senderAvatar(entity.getSenderAvatar())
                .sentTime(DateTimeUtils.getTime(entity.getSentTime()))
                .content(entity.getContent())
                .build();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
