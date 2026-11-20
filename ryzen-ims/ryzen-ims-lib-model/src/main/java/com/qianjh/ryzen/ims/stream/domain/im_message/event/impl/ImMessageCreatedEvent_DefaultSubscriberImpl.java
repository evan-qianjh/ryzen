package com.qianjh.ryzen.ims.stream.domain.im_message.event.impl;

import com.qianjh.ryzen.ims.stream.domain.im_message.event.ImMessageCreatedEvent;

public class ImMessageCreatedEvent_DefaultSubscriberImpl implements ImMessageCreatedEvent.Subscriber {
    @Override
    public void subscribe(ImMessageCreatedEvent.Body body) {
        throw new UnsupportedOperationException();
    }
}
