package com.qianjh.ryzen.topic._private.ims_message.impl;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.websocket.netty.MessageSender;
import com.qianjh.ryzen.framework.websocket.service.PushMessageService;
import com.qianjh.ryzen.framework.websocket.topic.TopicMeta;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushInsertMessage;
import com.qianjh.ryzen.topic._private.ims_message.ImsMessagePusher;
import com.qianjh.ryzen.topic._private.ims_message.ImsMessageTopic;
import com.qianjh.ryzen.topic._private.ims_message.model.MessageScene;
import com.qianjh.ryzen.topic._private.ims_message.model.PushData;
import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImsMessagePusherImpl implements ImsMessagePusher {

    private final ImsMessageTopic imsMessageTopic;
    private final PushMessageService pushMessageService;

    @Override
    public void push(PushInsertMessage.Body payload) {

        TopicMeta topicMeta = imsMessageTopic.getTopicMeta();

        PushInsertMessage.Body.Conversation conversation = payload.conversation();

        // 接待人账户
        Set<Long> accountIds = Set.of(conversation.serviceAccountId());

        // 获取接待账户订阅者
        List<ChannelId> subscribers = imsMessageTopic.getSubscribers(payload.oemId(), accountIds, topicMeta);
        if (subscribers.isEmpty()) {
            return;
        }

        // 构建推送数据
        PushData data = buildData(payload);

        // 构建报文
        com.qianjh.ryzen.framework.websocket.message.Message message = pushMessageService.build(data, topicMeta, MessageScene.INSERT.name());

        // 发送
        MessageSender.send(subscribers, message, topicMeta);
    }

    private PushData buildData(PushInsertMessage.Body message) {
        PushInsertMessage.Body.Conversation conversation = message.conversation();

        return PushData.builder()
                .id(IdUtils.toString(message.id()))
                .seq(IdUtils.toString(message.seq()))
                .direction(message.direction())
                .senderName(message.senderName())
                .senderAvatar(message.senderAvatar())
                .content(message.content())
                .conversation(
                        PushData.Conversation.builder()
                                .id(IdUtils.toString(conversation.id()))
                                .avatar(conversation.avatar())
                                .name(conversation.name())
                                .build()
                )
                .build();
    }
}
