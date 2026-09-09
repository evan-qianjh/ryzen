package com.qianjh.ryzen.gateway.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 
 */
public interface RateLimitService {

    /**
     * 获取ip
     *
     * @param request 请求
     * @return IP
     */
    String getClientIp(ServerHttpRequest request);

}
