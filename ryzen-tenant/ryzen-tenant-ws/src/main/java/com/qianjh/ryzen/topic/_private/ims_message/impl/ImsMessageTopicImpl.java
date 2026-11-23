package com.qianjh.ryzen.topic._private.ims_message.impl;

import com.qianjh.ryzen.framework.websocket.topic.TopicMeta;
import com.qianjh.ryzen.topic._private.ims_message.ImsMessageTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImsMessageTopicImpl implements ImsMessageTopic {

    @Override
    public String getName() {
        return "private#ims_message";
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
