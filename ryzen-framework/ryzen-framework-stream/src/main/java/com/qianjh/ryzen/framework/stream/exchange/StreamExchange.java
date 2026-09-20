package com.qianjh.ryzen.framework.stream.exchange;

/**
 * @author QianJH
 */
public interface StreamExchange {

    /**
     * 请求
     *
     * @param type        类型
     * @param payloadJson 报文
     * @param oemId       OEM ID
     */
    void request(String type, String payloadJson, Long oemId);

    /**
     * 响应
     *
     * @param type  类型
     * @param body  报文
     * @param oemId OEM ID
     * @param <T>   报文泛型
     */
    <T> void response(String type, T body, Long oemId);
}
