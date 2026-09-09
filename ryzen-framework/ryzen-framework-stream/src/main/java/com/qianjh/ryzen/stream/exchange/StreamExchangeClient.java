package com.qianjh.ryzen.stream.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author QianJH
 */
public interface StreamExchangeClient {

    default Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 发布
     *
     * @param type     类型
     * @param body     报文
     * @param tenantId 租户
     * @param <T>      报文泛型
     */
    <T> void publish(String type, T body, Long tenantId);

    /**
     * 订阅
     *
     * @param type        类型
     * @param payloadJson 报文
     * @param tenantId    租户
     */
    void subscribe(String type, String payloadJson, Long tenantId);
}
