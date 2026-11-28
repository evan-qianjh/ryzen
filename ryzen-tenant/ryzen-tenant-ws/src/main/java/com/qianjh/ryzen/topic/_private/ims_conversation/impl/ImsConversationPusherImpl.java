package com.qianjh.ryzen.topic._private.ims_conversation.impl;

import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.websocket.netty.MessageSender;
import com.qianjh.ryzen.framework.websocket.service.PushMessageService;
import com.qianjh.ryzen.framework.websocket.topic.TopicMeta;
import com.qianjh.ryzen.tenant.stream.from_ims.push_conversation.PushConversationDispatched;
import com.qianjh.ryzen.topic._private.ims_conversation.ImsConversationPusher;
import com.qianjh.ryzen.topic._private.ims_conversation.ImsConversationTopic;
import com.qianjh.ryzen.topic._private.ims_conversation.model.PushData;
import com.qianjh.ryzen.topic._private.ims_conversation.model.Type;
import io.netty.channel.ChannelId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImsConversationPusherImpl implements ImsConversationPusher {

    private final PushMessageService pushMessageService;
    private final ImsConversationTopic imsConversationTopic;

    @Override
    public void push(PushConversationDispatched.Body body) {
        TopicMeta topicMeta = imsConversationTopic.getTopicMeta();

        //
        insert(body, topicMeta);

        //
        delete(body, topicMeta);
    }

    private void insert(PushConversationDispatched.Body body, TopicMeta topicMeta) {
        Long accountId = body.serviceAccountId();
        // 无
        if (accountId == null) {
            log.error("调度的对话无用户 ::: {}", body);
            return;
        }

        // 获取服务账户订阅者
        List<ChannelId> subscribers = imsConversationTopic.getSubscribers(body.oemId(), Set.of(accountId), topicMeta);
        if (subscribers.isEmpty()) {
            return;
        }

        // 构建推送数据
        PushData data = PushData.builder()
                .id(IdUtils.toString(body.id()))
                .imType(body.imType())
                .title(body.title())
                .subTitle(body.subTitle())
                .avatar(body.avatar())
                .lastMessageContent(body.lastMessageContent())
                .lastMessageContentType(body.lastMessageContentType())
                .lastMessageId(IdUtils.toString(body.lastMessageId()))
                .lastMessageTime(body.lastMessageTime())
                .matchTags(body.matchTags())
                .build();

        // 发送
        MessageSender.send(
                subscribers,
                pushMessageService.build(data, topicMeta, Type.INSERT.name())
                , topicMeta
        );
    }

    private void delete(PushConversationDispatched.Body body, TopicMeta topicMeta) {
        Long accountId = body.beforeServiceAccountId();

        // 无
        if (accountId == null) {
            return;
        }

        // 获取服务账户订阅者
        List<ChannelId> subscribers = imsConversationTopic.getSubscribers(body.oemId(), Set.of(accountId), topicMeta);
        if (subscribers.isEmpty()) {
            return;
        }

        // 构建推送数据
        PushData data = PushData.builder()
                .id(IdUtils.toString(body.id()))
                .build();

        // 发送
        MessageSender.send(
                subscribers,
                pushMessageService.build(data, topicMeta, Type.DELETE.name())
                , topicMeta
        );
    }
}
