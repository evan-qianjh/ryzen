package com.qianjh.ryzen.tenant.stream.from_ims.push_conversation;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.tenant.stream.from_ims.PushConversation;
import lombok.Builder;

import java.util.List;

/**
 *
 * @param <T>
 * @author QianJH
 */
public interface PushConversationDispatched<T> extends PushConversation<T> {
    String TYPE = "DISPATCHED";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            Long id,
            Long oemId,
            Long tenantId,
            String imType,
            String title,
            String subTitle,
            String avatar,
            String lastMessageContent,
            Long lastMessageId,
            Long lastMessageTime,
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
