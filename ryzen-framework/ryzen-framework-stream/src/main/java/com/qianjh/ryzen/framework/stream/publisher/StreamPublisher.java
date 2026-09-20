package com.qianjh.ryzen.framework.stream.publisher;

import com.qianjh.ryzen.framework.stream.model.Stream;

/**
 * @author QianJH
 */
public interface StreamPublisher<T> extends Stream<T> {

    void publish(T body);
}
