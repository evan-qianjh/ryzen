package com.qianjh.ryzen.gateway.filter;

import com.qianjh.ryzen.gateway.util.IpUtils;
import com.qianjh.ryzen.header.GatewayHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求头增加客户端IP-全局过滤器
 *
 * 
 */
@Slf4j
@Component
public class HeaderAddClientIpGlobalFilter implements GlobalFilter, Ordered {

    public static final Integer ORDER = ForgedRequestGlobalFilter.ORDER + 1;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String realIp = IpUtils.getClientIp(request);
        if (!StringUtils.hasLength(realIp)) {
            log.error("获取客户端IP失败 ::: ");
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return Mono.empty();
        }

        ServerHttpRequest.Builder nextRequestBuilder = request.mutate();
        nextRequestBuilder.header(GatewayHeader.CLIENT_IP, realIp);

        return chain.filter(exchange.mutate().request(nextRequestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
