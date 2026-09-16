package com.qianjh.ryzen.stream.publisher;

import com.qianjh.ryzen.stream.model.Stream;

/**
 * @author QianJH
 */
public interface StreamPublisher<T> extends Stream<T> {

    void publish(T body);
}
