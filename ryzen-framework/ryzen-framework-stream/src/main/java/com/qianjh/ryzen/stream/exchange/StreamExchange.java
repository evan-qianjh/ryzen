package com.qianjh.ryzen.stream.exchange;

/**
 * @author QianJH
 */
public interface StreamExchange {

    /**
     * 请求
     *
     * @param type        类型
     * @param payloadJson 报文
     * @param tenantId    租户ID
     */
    void request(String type, String payloadJson, Long tenantId);

    /**
     * 响应
     *
     * @param type     类型
     * @param body     报文
     * @param tenantId 租户
     * @param <T>      报文泛型
     */
    <T> void response(String type, T body, Long tenantId);
}
