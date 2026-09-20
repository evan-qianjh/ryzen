package com.qianjh.ryzen.framework.gateway.util;

import com.google.common.net.InternetDomainName;
import com.qianjh.ryzen.framework.common.header.ProxyHeader;
import com.qianjh.ryzen.framework.gateway.util.dto.Domain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.ObjectUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * 
 */
@Slf4j
public final class DomainUtils {

    /**
     * 精准域名请求头
     * 按照定义的顺序获取
     */
    private static final String[] PRECISE_DOMAIN_HEADER = new String[]{
            // waf、cdn附加
            ProxyHeader.OEM_DOMAIN,

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

    /**
     * 解析域名中的 OEM 和 Tenant。
     * 域名格式：
     * {tenant}.{client}.{oem}
     * 示例：
     * 123.admin-api.qianjh.com
     * 123.admin-api.qianjh.com.cn
     *
     * @param domain 请求域名，不包含端口
     * @return OEM 与 Tenant 信息
     */
    public static Domain parse(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Host must not be blank");
        }

        domain = normalize(domain);

        InternetDomainName name = InternetDomainName.from(domain);

        if (!name.hasPublicSuffix()) {
            throw new IllegalArgumentException(
                    "Invalid domain: " + domain
            );
        }

        InternetDomainName oemDomain = name.topPrivateDomain();

        String oem = oemDomain.toString();


        String[] split = domain.split("\\.");
        String tenant = split[0];

        return new Domain(oem, tenant);
    }

    private static String normalize(String host) {
        String domain = host.trim().toLowerCase(Locale.ROOT);

        // Host 可能是：
        // 123.admin-api.qianjh.com:8080
        int portIndex = domain.lastIndexOf(':');

        if (portIndex > -1) {
            domain = domain.substring(0, portIndex);
        }

        // FQDN：
        // 123.admin-api.qianjh.com.
        if (domain.endsWith(".")) {
            domain = domain.substring(0, domain.length() - 1);
        }

        return domain;
    }
}
