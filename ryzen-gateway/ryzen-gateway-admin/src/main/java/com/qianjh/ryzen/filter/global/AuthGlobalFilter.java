package com.qianjh.ryzen.filter.global;

import com.qianjh.ryzen.config.RsaProperties;
import com.qianjh.ryzen.config.SecurityProperties;
import com.qianjh.ryzen.gateway.filter.ForgedRequestGlobalFilter;
import com.qianjh.ryzen.header.GatewayHeaderAdmin;
import com.qianjh.ryzen.service.RyzenTokenService;
import com.qianjh.ryzen.service.TokenService;
import com.qianjh.ryzen.service.dto.TokenPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author QianJH
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    public static final Integer ORDER = Math.max(ForgedRequestGlobalFilter.ORDER, OemGlobalFilter.ORDER) + 1;

    private final static Pattern AUTH_PATH_PATTERN = Pattern.compile("^/[a-zA-Z0-9_-]+/public/");

    public static final String HEADER_TOKEN_KEY = "Authorization";

    private final TokenService tokenService;
    private final RyzenTokenService ryzenTokenService;
    private final SecurityProperties securityProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // tenant
        HttpHeaders headers = request.getHeaders();
        String tokenStr = headers.getFirst(HEADER_TOKEN_KEY);
        String oemIdStr = headers.getFirst(GatewayHeaderAdmin.OEM_ID);
        Long oemId = Objects.nonNull(oemIdStr) ? Long.parseLong(oemIdStr) : null;

        //
        String path = request.getPath().value();

        // 不符合这种格式的，需要验证token ： /{service}/public/xxx
        boolean authRequired = !AUTH_PATH_PATTERN.matcher(path).find();

        // 需要登录 & 未提供token
        if (authRequired && !StringUtils.hasText(tokenStr)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }

        // 验证token
        RsaProperties properties = securityProperties.getToken();
        RSAPublicKey rsaPublicKey = ryzenTokenService.getPublicKey(properties);
        TokenPayload tokenPayload = tokenService.parseTokenPayload(tokenStr, rsaPublicKey);
        if (authRequired && Objects.isNull(tokenPayload)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return Mono.empty();
        }

        // header add
        ServerHttpRequest.Builder nextRequestBuilder = request.mutate();
        if (Objects.nonNull(oemId) && Objects.nonNull(tokenPayload)) {
            Long tokenOemId = Objects.nonNull(tokenPayload.getOemId()) ? Long.parseLong(tokenPayload.getOemId()) : null;
            if (!oemId.equals(tokenOemId)) {
                log.warn("发现跨域OEM的token ::: oemId={}, tokenOemId={}", oemId, tokenOemId);
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return Mono.empty();
            }

            nextRequestBuilder.header(GatewayHeaderAdmin.TENANT_ID, tokenPayload.getTenantId());
            nextRequestBuilder.header(GatewayHeaderAdmin.ACCOUNT_ID, tokenPayload.getAccountId());
        }
        // remove header
        nextRequestBuilder.headers(h -> h.remove(HEADER_TOKEN_KEY));

        return chain.filter(exchange.mutate().request(nextRequestBuilder.build()).build());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}