package com.qianjh.ryzen.framework.stream.model;

/**
 * 流
 * @author QianJH
 */
public interface Stream<T> {
    /**
     * 获取领域，例如 user
     * @return 领域
     */
    String getDomain();

    /**
     * 获取类型，例如 user_created
     * @return 类型
     */
    String getType();

    /**
     * 获取报文体泛型
     * @return 泛型
     */
    Class<T> getBodyClass();
}
