package com.qianjh.ryzen.topic._private.ims_conversation.impl;

import com.qianjh.ryzen.framework.websocket.topic.TopicMeta;
import com.qianjh.ryzen.topic._private.ims_conversation.ImsConversationTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImsConversationTopicImpl implements ImsConversationTopic {

    @Override
    public String getName() {
        return "private#ims_conversation";
    }

    @Override
    public boolean isNeedLogin() {
        return true;
    }

    @Override
    public TopicMeta getTopicMeta() {
        return TopicMeta.ofName(getName());
    }

}
