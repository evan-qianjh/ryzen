package com.qianjh.ryzen.filter.global;

import com.qianjh.ryzen.gateway.filter.HeaderAddClientIpGlobalFilter;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * 记录请求日志
 *
 * @author QianJH
 */
@Slf4j
@Component
public class RequestLogGlobalFilter implements GlobalFilter, Ordered {

    private static final String LOG_START_TIME = "logStartTime";

    @Value("${trace.max-long-request-ms:300}")
    private long maxLongRequestMs;
    private static final Logger REQUEST_LOGGER = LoggerFactory.getLogger("REQUEST_LOGGER");

    /**
     * 在增加客户端IP过滤器之后
     */
    public static final Integer ORDER =
            Collections.max(
                    List.of(
                            OemGlobalFilter.ORDER,
                            HeaderAddClientIpGlobalFilter.ORDER
                    )
            ) + 1;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        String tenantId = headers.getFirst(GatewayHeaderAdmin.TENANT_ID);
        String clientIp = headers.getFirst(GatewayHeaderAdmin.CLIENT_IP);

        // 请求URL
        String path = request.getPath().toString();
        HttpMethod method = request.getMethod();

        // get请求不记录
        if (HttpMethod.GET == method) {
            return chain.filter(exchange);
        }

        REQUEST_LOGGER.info("TENANT={}, IP={}, API={}|{}", tenantId, clientIp, method, path);

        exchange.getAttributes().put(LOG_START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(LOG_START_TIME);
            if (startTime != null) {
                long executeTime = (System.currentTimeMillis() - startTime);
                if (executeTime >= maxLongRequestMs) {
                    REQUEST_LOGGER.warn("slow ::: TENANT={}, IP={}, API={}|{}, ms={}", tenantId, clientIp, method, path, executeTime);
                }
            }
        }));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
