package com.qianjh.ryzen.topic._private.ims_conversation.impl;

import com.qianjh.ryzen.entity.ImConversation;
import com.qianjh.ryzen.framework.common.util.IdUtils;
import com.qianjh.ryzen.framework.websocket.service.PushMessageService;
import com.qianjh.ryzen.service.ImConversationService;
import com.qianjh.ryzen.topic._private.ims_conversation.ImsConversationPusher;
import com.qianjh.ryzen.topic._private.ims_conversation.model.ConversationAction;
import com.qianjh.ryzen.topic._private.ims_conversation.model.PushData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImsConversationPusherImpl implements ImsConversationPusher {

    private final PushMessageService pushMessageService;
    private final ImConversationService conversationService;

    @Override
    public void push(ImConversation conversation) {
        // TODO 转移会话
    }

    private PushData buildData(ImConversation conversation) {
        return PushData.builder()
                .id(IdUtils.toString(conversation.getId()))
                .action(ConversationAction.DELETE)
                .build();
    }
}
