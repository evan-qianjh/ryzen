package com.qianjh.ryzen.gateway.filter;

import com.qianjh.ryzen.header.GatewayHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 伪造请求
 *
 *
 */
@Slf4j
@Component
public class ForgedRequestGlobalFilter implements GlobalFilter, Ordered {

    public static final Integer ORDER = Integer.MIN_VALUE;

    private final static String[] DANGER_HEADER_PREFIXES = {GatewayHeader.PREFIX};

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        for (String key : headers.headerNames()) {
            // 危险开头
            for (String dangerHeaderPrefix : DANGER_HEADER_PREFIXES) {
                if (key.startsWith(dangerHeaderPrefix)) {
                    log.warn("发现危险请求 ::: url={} {}, headers={}", request.getMethod().name(), request.getPath(), headers);
                    exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                    return Mono.empty();
                }
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
