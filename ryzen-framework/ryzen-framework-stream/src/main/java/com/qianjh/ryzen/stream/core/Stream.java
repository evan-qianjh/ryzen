package com.qianjh.ryzen.stream.core;

/**
 *
 * @author QianJH
 */
public interface Stream<T> {
    String getDomain();

    String getType();

    Class<T> getBodyClass();
}
