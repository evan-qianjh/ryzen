package com.qianjh.ryzen.stream.event;

/**
 * @author QianJH
 */
public interface Event<T> {
    String getDomain();

    String getType();

    Class<T> getBodyClass();
}
