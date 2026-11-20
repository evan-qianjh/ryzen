package com.qianjh.ryzen.stream;

import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.framework.common.util.GsonUtils;
import com.qianjh.ryzen.framework.stream.consumer.StreamConsumer;
import com.qianjh.ryzen.model.SchedulerMessage;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.ImMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class ImsSchedulerMessage_StreamConsumer implements StreamConsumer {

    @Bean
    public Consumer<Message<String>> imsSchedulerMessageSource(
            ImAccountService imAccountService,
            ImConversationService conversationService,
            ImMessageService messageService
    ) {
        return message -> {
            MessageHeaders headers = message.getHeaders();
            String payload = message.getPayload();
            log.info("message ::: {}", message);

            SchedulerMessage schedulerMessage = GsonUtils.GSON.fromJson(payload, SchedulerMessage.class);


            // TODO saas
            ImAccount imAccount = imAccountService.getByUk(schedulerMessage.imType(), schedulerMessage.accountCode());
            if (imAccount == null) {
                log.error("未创建ImAccount ::: {}", payload);
                return;
            }

            // 对话
            ImConversation conversation = conversationService.createIfAbsent(imAccount, schedulerMessage.conversationCode());

            // 消息
            ImMessage messageEntity = messageService.createIfAbsent(imAccount, conversation, schedulerMessage);

            conversationService.recordLastMessage(conversation, messageEntity);
        };
    }
}
