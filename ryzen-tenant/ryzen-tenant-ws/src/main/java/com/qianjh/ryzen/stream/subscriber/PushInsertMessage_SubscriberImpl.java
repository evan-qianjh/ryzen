package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type.PushInsertMessage;
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
public class PushInsertMessage_SubscriberImpl implements PushInsertMessage.Subscriber {

    private final ImsMessagePusher imsMessagePusher;

    @Override
    public void subscribe(PushInsertMessage.Body body) {
        log.info("消费 PushInsertMessage ::: {}", body);
        imsMessagePusher.push(body);
    }
}
