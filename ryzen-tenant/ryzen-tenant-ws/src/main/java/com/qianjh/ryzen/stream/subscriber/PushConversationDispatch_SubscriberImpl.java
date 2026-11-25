package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation.type.PushConversationDispatch;
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
public class PushConversationDispatch_SubscriberImpl implements PushConversationDispatch.Subscriber {

    private final ImsConversationPusher imsConversationPusher;

    @Override
    public void subscribe(PushConversationDispatch.Body body) {
        log.info("消费 PushConversationDispatch ::: {}", body);
        imsConversationPusher.push(body);
    }
}
