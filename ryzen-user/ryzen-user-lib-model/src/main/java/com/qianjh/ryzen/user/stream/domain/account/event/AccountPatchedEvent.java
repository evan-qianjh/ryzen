package com.qianjh.ryzen.user.stream.domain.account.event;

import com.qianjh.ryzen.framework.stream.subscriber.StreamSubscriber;
import com.qianjh.ryzen.user.stream.domain.account.AccountEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账户修改事件
 *
 * @param <T>
 * @author QianJH
 */
public interface AccountPatchedEvent<T> extends AccountEvent<T> {
    String TYPE = "patched";

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
