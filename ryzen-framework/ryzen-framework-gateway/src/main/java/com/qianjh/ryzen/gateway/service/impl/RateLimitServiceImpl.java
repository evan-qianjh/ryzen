package com.qianjh.ryzen.gateway.service.impl;

import com.qianjh.ryzen.gateway.service.RateLimitService;
import com.qianjh.ryzen.header.GatewayHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

/**
 * 
 */
@Slf4j
@Service
public class RateLimitServiceImpl implements RateLimitService {

    @Override
    public String getClientIp(ServerHttpRequest request) {
        String val = request.getHeaders().getFirst(GatewayHeader.CLIENT_IP);
        if (log.isDebugEnabled()) {
            log.debug("get clientIp ::: {}", val);
        }
        return val;
    }

}
