package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.ims.stream.domain.im_conversation_dispatched.type.ImConversationDispatchedCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImConversationDispatchedCreatedEvent_SubscriberImpl implements ImConversationDispatchedCreatedEvent.Subscriber {

    @Override
    public void subscribe(ImConversationDispatchedCreatedEvent.Body body) {
        log.debug("消费 ImConversationDispatchedCreatedEvent ::: {}", body);

        // TODO 通知tenant-ws发消息
    }
}
