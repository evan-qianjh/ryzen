package com.qianjh.ryzen.topic._private.ims_message.impl;

import com.qianjh.ryzen.entity.Conversation;
import com.qianjh.ryzen.entity.Message;
import com.qianjh.ryzen.framework.websocket.netty.MessageSender;
import com.qianjh.ryzen.framework.websocket.service.PushMessageService;
import com.qianjh.ryzen.framework.websocket.topic.TopicMeta;
import com.qianjh.ryzen.service.ConversationService;
import com.qianjh.ryzen.topic._private.ims_message.ImsMessagePusher;
import com.qianjh.ryzen.topic._private.ims_message.ImsMessageTopic;
import com.qianjh.ryzen.topic._private.ims_message.model.PushData;
import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImsMessagePusherImpl implements ImsMessagePusher {

    private final ImsMessageTopic imsMessageTopic;
    private final PushMessageService pushMessageService;
    private final ConversationService conversationService;

    @Override
    public void push(Message message) {
        Long oemId = message.getOemId();
        Long tenantId = message.getTenantId();

        TopicMeta topicMeta = imsMessageTopic.getTopicMeta();

        // 查出消息对话
        Conversation conversation = conversationService.getById(oemId, tenantId, message.getConversationId());
        if (conversation == null) {
            log.error("对话不存在 ::: messageId={}, conversationId={}", message.getId(), message.getConversationId());
            return;
        }

        // 接待人账户
        Long receptionAccountId = conversation.getReceptionAccountId();

        // 获取接待账户订阅者
        List<ChannelId> subscribers = imsMessageTopic.getSubscribers(tenantId, receptionAccountId, topicMeta);
        if (subscribers.isEmpty()) {
            return;
        }

        // 构建推送数据
        PushData data = buildData(message);

        // 构建报文
        com.qianjh.ryzen.framework.websocket.message.Message message1 = pushMessageService.build(data, topicMeta);

        // 发送
        MessageSender.send(subscribers, message1, topicMeta);
    }

    private PushData buildData(Message message) {
        return PushData.builder()
                .direction(message.getDirection())
                .senderName(message.getSenderName())
                .senderAvatar(message.getSenderAvatar())
                .content(message.getContent())
                .build();
    }
}
