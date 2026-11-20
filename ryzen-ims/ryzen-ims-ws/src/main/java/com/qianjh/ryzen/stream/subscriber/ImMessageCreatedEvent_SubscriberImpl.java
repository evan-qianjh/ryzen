package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.entity.ImMessage;
import com.qianjh.ryzen.ims.stream.domain.im_message.event.ImMessageCreatedEvent;
import com.qianjh.ryzen.service.ImMessageService;
import com.qianjh.ryzen.topic._private.ims_message.ImsMessagePusher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImMessageCreatedEvent_SubscriberImpl implements ImMessageCreatedEvent.Subscriber {

    private final ImsMessagePusher imsMessagePusher;
    private final ImMessageService imMessageService;

    @Override
    public void subscribe(ImMessageCreatedEvent.Body body) {
        log.debug("消费 ImMessageCreatedEvent ::: {}", body);

        ImMessage message = imMessageService.getById(body.getOemId(), body.getTenantId(), body.getId());
        if (message == null) {
            log.error("ImMessage不存在 ::: {}", body);
            return;
        }

        imsMessagePusher.push(message);
    }
}
