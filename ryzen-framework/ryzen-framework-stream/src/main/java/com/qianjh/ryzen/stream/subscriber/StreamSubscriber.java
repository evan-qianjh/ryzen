package com.qianjh.ryzen.stream.subscriber;

import com.qianjh.ryzen.stream.model.Stream;
import com.qianjh.ryzen.stream.util.StreamUtils;

/**
 * @author QianJH
 */
public interface StreamSubscriber<T> extends Stream<T> {

    void subscribe(T body);

    default void subscribeRaw(String payload) {
        T body = StreamUtils.fromJson(payload, getBodyClass()).getBody();
        subscribe(body);
    }
}
