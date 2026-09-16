package com.qianjh.ryzen.stream.core;

/**
 * @author QianJH
 */
public interface StreamSubscriberDispatcher {
    /**
     * 分发
     *
     * @param domain  领域
     * @param type    类型
     * @param payload 报文
     */
    void dispatch(String domain, String type, String payload);
}
