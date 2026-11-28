package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.tenant.stream.from_ims.push_message.PushMessageInsert;
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
public class PushMessageInsertSubscriberImpl implements PushMessageInsert.Subscriber {

    private final ImsMessagePusher imsMessagePusher;

    @Override
    public void subscribe(PushMessageInsert.Body body) {
        log.info("消费 PushMessageInsert ::: {}", body);
        imsMessagePusher.push(body);
    }
}
