package com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business.type;

import com.qianjh.ryzen.framework.stream.publisher.StreamPublisher;
import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.tenant.enums.AccountBusinessStatus;
import com.qianjh.ryzen.tenant.enums.AccountBusinessType;
import com.qianjh.ryzen.tenant.stream.topic.account.domain.account_business.AccountBusiness;
import com.qianjh.ryzen.tenant.stream.topic.from_ims.domain.push_message.PushMessage;
import lombok.Builder;

/**
 *
 * @param <T>
 * @author QianJH
 */
public interface AccountBusinessPatched<T> extends AccountBusiness<T> {
    String TYPE = "patched";

    /**
     * 报文体
     */
    @Builder(toBuilder = true)
    record Body(
            Long oemId,
            Long tenantId,
            Long id,
            Long accountId,
            AccountBusinessType type,

            //
            AccountBusinessStatus status

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
