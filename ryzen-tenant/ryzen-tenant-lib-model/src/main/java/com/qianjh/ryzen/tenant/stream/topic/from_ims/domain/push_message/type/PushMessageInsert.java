package com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.type;

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
public interface PushMessageInsert<T> extends PushMessage<T> {
    String TYPE = "insert";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            Long oemId,
            Long tenantId,
            Long id,
            Long seq,
            String direction,
            String senderName,
            String senderAvatar,
            String content,
            Long sentTime,
            Conversation conversation
    ) {
        @Builder(toBuilder = true)
        public record Conversation(
                Long id,
                String imType,
                String name,
                String avatar,
                List<String> matchTags,
                Long serviceAccountId
        ) {

        }
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
