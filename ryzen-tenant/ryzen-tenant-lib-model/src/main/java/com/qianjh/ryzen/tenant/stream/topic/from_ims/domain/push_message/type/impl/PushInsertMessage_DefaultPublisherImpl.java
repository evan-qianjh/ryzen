package com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.impl;

import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushInsertMessage;

/**
 * 默认实现
 *
 * @author QianJH
 */
public class PushInsertMessage_DefaultPublisherImpl implements PushInsertMessage.Publisher {

    @Override
    public void publish(PushInsertMessage.Body body) {
        throw new UnsupportedOperationException();
    }
}
