package com.qianjh.ryzen.stream.event;

/**
 * @author QianJH
 */
public interface EventPublisher<T> extends Event<T> {

    void publish(T body);
}
