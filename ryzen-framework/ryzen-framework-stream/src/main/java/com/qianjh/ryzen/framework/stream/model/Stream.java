package com.qianjh.ryzen.framework.stream.model;

/**
 *
 * @author QianJH
 */
public interface Stream<T> {
    String getDomain();

    String getType();

    Class<T> getBodyClass();
}
