package com.qianjh.ryzen.filter.global;

import com.qianjh.ryzen.cache.OemDomainCache;
import com.qianjh.ryzen.entity.OemDomain;
import com.qianjh.ryzen.gateway.filter.ForgedRequestGlobalFilter;
import com.qianjh.ryzen.gateway.util.DomainUtils;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author QianJH
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class OemGlobalFilter implements GlobalFilter, Ordered {

    public static final Integer ORDER = ForgedRequestGlobalFilter.ORDER + 1;

    private final OemDomainCache oemDomainCache;

    private static final Set<String> LOG_IGNORE_HEADERS = new HashSet<>();

    {
        LOG_IGNORE_HEADERS.add("token");
        LOG_IGNORE_HEADERS.add("authorization");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        RequestPath path = request.getPath();
        String method = request.getMethod().name();
        String url = String.format("%s %s", method, path);

        HttpHeaders headers = request.getHeaders();
        String domain = DomainUtils.getDomain(request);

        // 没有获取到domain
        if (!StringUtils.hasLength(domain)) {
            List<Map.Entry<String, String>> logHeaders = headers.toSingleValueMap().entrySet().stream()
                    .filter(entry -> !LOG_IGNORE_HEADERS.contains(entry.getKey().toLowerCase()))
                    .collect(Collectors.toList());
            log.error("获取远程地址失败 ::: url={}, headers={}", url, logHeaders);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return Mono.empty();
        }

        // 解析 host
        OemDomain tenantDomain = oemDomainCache.getByDomain(domain);
        if (Objects.isNull(tenantDomain)) {
            String rootDomain = DomainUtils.extractRoot(domain);
            tenantDomain = oemDomainCache.getByDomain(rootDomain);
        }

        // 解析根域名
        if (Objects.isNull(tenantDomain)) {
            log.error("获取OEM失败 ::: domain={}, url={}", domain, url);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return Mono.empty();
        }

        //
        Long oemId = tenantDomain.getOemId();
        Assert.notNull(oemId, "");

        //
        ServerHttpRequest.Builder nextRequestBuilder = request.mutate();
        nextRequestBuilder.header(GatewayHeaderAdmin.OEM_ID, oemId.toString());
        log.debug("获取OEM成功 ::: url={}, oemId={}", url, oemId);

        return chain.filter(exchange.mutate().request(nextRequestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
