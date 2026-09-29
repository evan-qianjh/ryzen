package com.qianjh.ryzen.filter.dto;

/**
 * gateway message code
 */
public enum GwMc {
    /**
     * 缺少请求头 x-apikey
     */
    AUTH_001,
    /**
     * 缺少请求头 x-timestamp
     */
    AUTH_002,
    /**
     * 缺少请求头 x-recvwindow
     */
    AUTH_003,
    /**
     * 错误的请求头 x-recvwindow
     */
    AUTH_004,
    /**
     * 缺少请求头 x-algorithm
     */
    AUTH_005,
    /**
     * 错误的请求头 x-algorithm
     */
    AUTH_006,
    /**
     * 缺少请求头 x-signature
     */
    AUTH_007,


    /**
     * ApiKey不存在
     */
    AUTH_101,
    /**
     * ApiKey未激活
     */
    AUTH_102,
    /**
     * 签名错误
     */
    AUTH_103,
    /**
     * 非绑定IP请求
     */
    AUTH_104,
    /**
     * 报文过时
     */
    AUTH_105,
    /**
     * 超出apikey权限
     */
    AUTH_106,
}
