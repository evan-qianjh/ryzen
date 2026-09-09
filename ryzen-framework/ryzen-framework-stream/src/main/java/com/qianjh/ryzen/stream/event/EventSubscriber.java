package com.qianjh.ryzen.stream.event;

import com.qianjh.ryzen.stream.util.StreamUtils;

/**
 * @author QianJH
 */
public interface EventSubscriber<T> extends Event<T> {

    void subscribe(T body);

    default void subscribeRaw(String payload) {
        T body = StreamUtils.fromJson(payload, getBodyClass()).getBody();
        subscribe(body);
    }
}
