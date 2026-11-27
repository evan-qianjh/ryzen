package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.SchedulerMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.ims.dto.ExecutorMessage;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportMessage;
import com.qianjh.ryzen.ims.stream.scheduler_to_app.report.SchedulerToAppReportMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectedMessageService {
    private final SchedulerMessageService schedulerMessageService;
    private final SchedulerToAppReportMessage.Publisher schedulerToAppReportMessagePublisher;

    public void forward(ExecutorToSchedulerReportMessage.Body body) {
        if (body == null || blank(body.executorCode()) || blank(body.accountCode())
                || blank(body.messageCode()) || blank(body.conversationCode())) {
            log.warn("Ignore invalid collected message: {}", body);
            return;
        }

        // 落地去重
        SchedulerMessage entity = schedulerMessageService.getByUk(body.imType(), body.messageCode());
        if (entity != null) {
            return;
        }

        SchedulerMessage schedulerMessage = schedulerMessageService.create(body);

        // 上报
        schedulerToAppReportMessagePublisher.publish(convert(schedulerMessage));
        log.debug("Collected message forwarded: messageCode={}, accountCode={}, conversationCode={}",
                body.messageCode(), body.accountCode(), body.conversationCode());

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
