package com.qianjh.ryzen.ims.stream.domain.im_message.event.impl;

import com.qianjh.ryzen.ims.stream.domain.im_message.event.ImMessageCreatedEvent;

public class ImMessageCreatedEvent_DefaultPublisherImpl implements ImMessageCreatedEvent.Publisher {

    @Override
    public void publish(ImMessageCreatedEvent.Body body) {
        throw new UnsupportedOperationException();
    }
}
