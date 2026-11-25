package com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_conversation.type;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.PushMessage;
import lombok.Builder;

import java.util.List;

/**
 *
 * @param <T>
 * @author QianJH
 */
public interface PushConversationDispatch<T> extends PushMessage<T> {
    String TYPE = "dispatch";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            Long id,
            Long oemId,
            Long tenantId,
            String imType,
            String name,
            String avatar,
            String lastMessageContent,
            Long lastMessageId,
            List<String> matchTags,
            Long serviceAccountId,
            Long beforeServiceAccountId
    ) {

    }

    /**
     * 发布者
     */
    interface Publisher extends StreamPublisher<Body> {
        @Override
        default String getDomain() {
            return DOMAIN;
        }

        @Override
        default String getType() {
            return TYPE;
        }

        @Override
        default Class<Body> getBodyClass() {
            return Body.class;
        }

        void publish(Body body);
    }

    /**
     * 订阅者
     */
    interface Subscriber extends StreamSubscriber<Body> {
        @Override
        default String getDomain() {
            return DOMAIN;
        }

        @Override
        default String getType() {
            return TYPE;
        }

        @Override
        default Class<Body> getBodyClass() {
            return Body.class;
        }
    }
}
