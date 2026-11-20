package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.SchedulerMessage;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.model.ExecutorMessage;
import com.qianjh.ryzen.transport.KafkaSchedulerTransport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectedMessageService {
    private final KafkaSchedulerTransport transport;
    private final SchedulerMessageService schedulerMessageService;

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

        try {
            transport.schedulerMessage(convert(schedulerMessage)).get(10, TimeUnit.SECONDS);
            log.debug("Collected message forwarded: messageCode={}, accountCode={}, conversationCode={}",
                    event.messageCode(), event.accountCode(), event.conversationCode());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while forwarding messageCode=" + event.messageCode(), e);
        } catch (Exception e) {
            log.error("Failed to forward collected message: messageCode={}", event.messageCode(), e);
            throw new IllegalStateException("Failed to forward messageCode=" + event.messageCode(), e);
        }
    }

    private com.qianjh.ryzen.model.SchedulerMessage convert(SchedulerMessage entity) {
        return com.qianjh.ryzen.model.SchedulerMessage.builder()
                .imType(entity.getImType())
                .direction(entity.getDirection())
                .accountCode(entity.getAccountCode())
                .messageCode(entity.getMessageCode())
                .conversationCode(entity.getConversationCode())
                .senderCode(entity.getSenderCode())
                .senderAvatar(entity.getSenderAvatar())
                .sentTime(DateTimeUtils.getTime(entity.getSentTime()))
                .content(entity.getContent())
                .build();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
