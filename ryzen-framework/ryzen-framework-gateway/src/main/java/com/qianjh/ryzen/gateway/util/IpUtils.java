package com.qianjh.ryzen.gateway.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 
 */
@Slf4j
public final class IpUtils {

    /**
     * 精准IP头
     * 按照定义的顺序获取
     * 国内：cn2 -> cf -> lb
     * 国外：cf -> lb
     */
    private static final String[] PRECISE_IP_HEADER = new String[]{
            // CN2
            "Zen-Client-Ip",

            // CF
            "CF-Connecting-IP",
            "True-Client-IP",

            // SLB / NGINX
            "X-Real-IP"
    };
    /**
     * 链路IP头
     */
    private static final String CHAIN_IP_HEADER = "X-Forwarded-For";

    /**
     * 获取客户端IP
     *
     * @param request request
     * @return ip
     */
    public static String getClientIp(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        String path = request.getPath().value();
        String method = Objects.requireNonNull(request.getMethod()).name();
        String url = String.format("%s %s", method, path);

        // 从精准IP头中获取
        for (String header : PRECISE_IP_HEADER) {
            String ip = headers.getFirst(header);
            if (isNotBlank(ip)) {
                log.debug("GET LV1 IP ::: header={}, ip={}, url={}", header, ip, url);
                return ip;
            }
        }

        // 从链路IP头中获取
        String ipChain = headers.getFirst(CHAIN_IP_HEADER);
        if (isNotBlank(ipChain)) {
            Assert.notNull(ipChain, CHAIN_IP_HEADER + " is null");

            // 按照都好分割ip
            String[] ips = ipChain.split(",");
            Assert.notEmpty(ips, CHAIN_IP_HEADER + " is empty");

            // 取第一位
            String ip = ips[0];
            log.debug("GET LV2 IP ::: header={}, ip={}, url={}, ipChain={}, headers={}", CHAIN_IP_HEADER, ip, url, ipChain, headers);
            return ip;
        }

        // 从远程地址中获取
        String ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        log.debug("GET LV3 IP ::: ip={}, url={}, headers={}", ip, url, headers);
        return ip;
    }

    /**
     * 为空
     *
     * @param ip ip
     * @return 是否
     */
    private static Boolean isBlank(String ip) {
        return !StringUtils.hasLength(ip) || "unknown".equalsIgnoreCase(ip.trim());
    }

    /**
     * 不为空
     *
     * @param ip ip
     * @return 是否
     */
    private static Boolean isNotBlank(String ip) {
        return !isBlank(ip);
    }
}
