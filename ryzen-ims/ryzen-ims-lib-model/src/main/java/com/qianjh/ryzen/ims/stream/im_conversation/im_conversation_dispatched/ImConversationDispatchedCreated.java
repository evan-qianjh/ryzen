package com.qianjh.ryzen.ims.stream.im_conversation.im_conversation_dispatched;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.ims.enums.ImConversationDispatchType;
import com.qianjh.ryzen.ims.stream.im_conversation.ImConversationDispatched;
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
public interface ImConversationDispatchedCreated<T> extends ImConversationDispatched<T> {
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
        private String remark;
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
