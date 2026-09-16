package com.qianjh.ryzen.stream.model;

/**
 *
 * @author QianJH
 */
public interface Stream<T> {
    String getDomain();

    String getType();

    Class<T> getBodyClass();
}
