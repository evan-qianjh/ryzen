package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.tenant.stream.from_ims.push_conversation.PushConversationDispatched;
import com.qianjh.ryzen.topic._private.ims_conversation.ImsConversationPusher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PushConversationDispatchSubscriberImpl implements PushConversationDispatched.Subscriber {

    private final ImsConversationPusher imsConversationPusher;

    @Override
    public void subscribe(PushConversationDispatched.Body body) {
        log.info("消费 PushConversationDispatch ::: {}", body);
        imsConversationPusher.push(body);
    }
}
