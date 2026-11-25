package com.qianjh.ryzen.ims.stream.domain.im_conversation_dispatched.type;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;
import com.qianjh.ryzen.ims.stream.domain.im_message.ImMessageEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户创建事件
 *
 * @param <T>
 * @author QianJH
 */
public interface ImConversationDispatchedCreatedEvent<T> extends ImMessageEvent<T> {
    String TYPE = "created";

    /**
     * 报文体
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    class Body {
        private Long id;
        private Long oemId;
        private Long tenantId;

        private Long imConversationId;
        private Long serviceAccountId;
        private Long beforeServiceAccountId;
        private ImConversationDispatchType type;
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
