package com.qianjh.ryzen.gateway.util;

import com.qianjh.ryzen.header.ProxyHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * 
 */
@Slf4j
public final class DomainUtils {

    private static final char CHAT_POINT = '.';

    /**
     * 提取根域名
     *
     * @param domain 域名 www.qianjh.com
     * @return 根域名 qianjh.com
     */
    public static String extractRoot(String domain) {
        Assert.isTrue(!ObjectUtils.isEmpty(domain), "domain is null");

        int index = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = domain.length() - 1; i >= 0; i--) {
            char c = domain.charAt(i);
            if (CHAT_POINT == c) {
                index++;
            }
            if (index > 1) {
                break;
            }
            builder.append(domain.charAt(i));
        }
        builder.reverse();
        return builder.toString();
    }


    /**
     * 精准域名请求头
     * 按照定义的顺序获取
     */
    private static final String[] PRECISE_DOMAIN_HEADER = new String[]{
            // waf、cdn附加
            ProxyHeader.TENANT_DOMAIN,

            //
            "Host",
    };
    /**
     * 链路IP头
     */
    private static final String CHAIN_DOMAIN_HEADER = "X-Forwarded-Host";

    /**
     * 获取HOST
     *
     * @param request request
     * @return host
     */
    public static String getDomain(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        String path = request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod()).name();
        String url = String.format("%s %s", method, path);

        // 从精准IP头中获取
        for (String header : PRECISE_DOMAIN_HEADER) {
            String domain = headers.getFirst(header);
            if (!ObjectUtils.isEmpty(domain)) {
                log.debug("GET DOMAIN ::: header={}, domain={}, url={}", header, domain, url);
                return domain;
            }
        }

        // 从链路IP头中获取
        String domain = headers.getFirst(CHAIN_DOMAIN_HEADER);
        if (!ObjectUtils.isEmpty(domain)) {
            log.debug("GET DOMAIN ::: header={}, domain={}, url={}", CHAIN_DOMAIN_HEADER, domain, url);
            return domain;
        }

        return null;
    }

}
