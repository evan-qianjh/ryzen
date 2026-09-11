package com.qianjh.ryzen.config;

import com.qianjh.ryzen.gateway.util.IpUtils;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.util.Md5Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author QianJH
 */
@Configuration
public class RateLimitConfig {
    private static final String METADATA_RATE_LIMIT_PATH = "rate-limit-path";

    /**
     * @return keyResolver
     */
    @Bean(name = "ipRateLimiterKeyResolver")
    public KeyResolver ipRateLimiterKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String key = IpUtils.getClientIp(request);
            if (key == null) {
                return Mono.empty();
            }

            HttpMethod method = request.getMethod();
            String path = getRateLimitPath(exchange);

            String limitKey = DigestUtils.md5Hex(method.name() + path + key);

            return Mono.just(limitKey);
        };
    }

    /**
     * 以请求头中的账户为key的键选择器
     *
     * @return keyResolver
     */
    @Primary
    @Bean(name = "accountRateLimiterKeyResolver")
    public KeyResolver accountRateLimiterKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String account = request.getHeaders().getFirst(GatewayHeaderAdmin.ACCOUNT_ID);
            if (StringUtils.isBlank(account)) {
                return Mono.empty();
            }
            HttpMethod method = request.getMethod();
            String path = getRateLimitPath(exchange);

            String limitKeyFormat = String.format("%s#%s#%s", method.name(), path, account);
            String limitKey = Md5Utils.encrypt(limitKeyFormat);

            return Mono.just(limitKey);
        };
    }

    /**
     * 获取限流路径
     *
     * @param exchange exchange
     * @return path
     */
    private String getRateLimitPath(ServerWebExchange exchange) {
        // 获取 route
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        Assert.notNull(route, "get exchange attribute fail : ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR");

        // 从metadata获取自定义path
        Object obj = route.getMetadata().getOrDefault(METADATA_RATE_LIMIT_PATH, "null");
        if (!"null".equals(obj)) {
            return obj.toString();
        }

        // 默认
        return exchange.getRequest().getPath().value();
    }

}
