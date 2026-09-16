package com.qianjh.ryzen.stream.core;

/**
 * @author QianJH
 */
public interface StreamPublisher<T> extends Stream<T> {

    void publish(T body);
}
