package com.qianjh.ryzen.filter.global;

import com.qianjh.ryzen.framework.common.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.framework.gateway.filter.ForgedRequestGlobalFilter;
import com.qianjh.ryzen.framework.gateway.util.DomainUtils;
import com.qianjh.ryzen.framework.saas.entity.Oem;
import com.qianjh.ryzen.framework.saas.entity.OemDomain;
import com.qianjh.ryzen.framework.saas.entity.Tenant;
import com.qianjh.ryzen.framework.saas.entity.TenantDomain;
import com.qianjh.ryzen.service.DomainService;
import com.qianjh.ryzen.service.dto.DomainOwner;
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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author QianJH
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SaasGlobalFilter implements GlobalFilter, Ordered {

    public static final Integer ORDER = ForgedRequestGlobalFilter.ORDER + 1;

    private final DomainService domainService;

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

        // resolve domain
        DomainOwner domainOwner = domainService.resolve(domain);
        if(domainOwner == null) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.empty();
        }
        // Oem
        Oem oem = domainOwner.getOem();
        if(oem == null || !oem.getEnabled()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.empty();
        }
        // OemDomain
        OemDomain oemDomain = domainOwner.getOemDomain();
        if(oemDomain == null || !oemDomain.getEnabled()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.empty();
        }
        // Tenant
        Tenant tenant = domainOwner.getTenant();
        if(tenant == null || !tenant.getEnabled()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.empty();
        }
        // TenantDomain
        TenantDomain tenantDomain = domainOwner.getTenantDomain();
        if(tenantDomain == null || !tenantDomain.getEnabled()) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return Mono.empty();
        }

        // next
        ServerHttpRequest.Builder nextRequestBuilder = request.mutate();
        nextRequestBuilder.header(GatewayHeaderAdmin.OEM_ID, oem.getId().toString());
        nextRequestBuilder.header(GatewayHeaderAdmin.TENANT_ID, tenant.getId().toString());

        return chain.filter(exchange.mutate().request(nextRequestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
