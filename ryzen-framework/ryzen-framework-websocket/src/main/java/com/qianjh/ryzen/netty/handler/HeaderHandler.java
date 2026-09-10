package com.qianjh.ryzen.netty.handler;

import com.qianjh.ryzen.exception.BusinessException;
import com.qianjh.ryzen.login.TokenParsed;
import com.qianjh.ryzen.login.TokenParser;
import com.qianjh.ryzen.login.TokenParserHolder;
import com.qianjh.ryzen.util.IdUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author QianJH
 */
@Slf4j
public class HeaderHandler extends ChannelInboundHandlerAdapter {

    public static final String GATEWAY = "gateway";
    public static final String TENANT_ID = "ga-tenant-id";
    public static final String ACCOUNT_ID = "ga-account-id";
    public static final String CLIENT_IP = "ga-client-ip";
    public static final String UPSTREAM_IP = "ga-upstream-ip";
    public static final String TOKEN = "token";

    private static final Long TENANT_COMPATIBLE_ID = 1L;

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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //
        if (msg instanceof FullHttpRequest httpRequest) {
            HttpHeaders headers = httpRequest.headers();

            // 从精准IP头中获取
            for (String header : PRECISE_IP_HEADER) {
                String ip = headers.get(header);
                if (!isBlank(ip)) {
                    log.debug("GET Client IP ::: clientId={}, ip={}, header={}", ctx.channel().id().asShortText(), ip, header);
                    ctx.channel().attr(AttributeKey.valueOf(CLIENT_IP)).set(ip);
                    break;
                }
            }

            // IP
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            String upstreamIp = socketAddress.getAddress().getHostAddress();

            log.debug("GET Client IP ::: clientId={}, ip={}", ctx.channel().id().asShortText(), upstreamIp);
            ctx.channel().attr(AttributeKey.valueOf(UPSTREAM_IP)).set(upstreamIp);


            // 租户
            String gateway = headers.get(GATEWAY);
            String tenantId = headers.get(TENANT_ID);

            // 如果非来自网关（只有平台自己出于性能考虑）
            if (StringUtils.isBlank(gateway)) {
                tenantId = String.valueOf(TENANT_COMPATIBLE_ID);
                log.debug("获取租户失败, 非来自网关请求, 默认{}={}", TENANT_ID, tenantId);
            } else {
                if (StringUtils.isBlank(tenantId)) {
                    log.error("获取租户失败, {}={}, headers={}", GATEWAY, gateway, headers);
                    throw new RuntimeException("parse domain error");
                } else {
                    log.debug("获取租户成功, {}={}, {}={}", GATEWAY, gateway, TENANT_ID, tenantId);
                }
            }

            // header获取到网关给的租户ID放到attr中
            ctx.channel().attr(AttributeKey.valueOf(TENANT_ID)).set(tenantId);


            // 获取token
            String token = getToken(httpRequest);

            // 处理token
            if (StringUtils.isNotBlank(token)) {
                handleToken(token, ctx);
            }
        }

        // next
        ctx.fireChannelRead(msg);
    }

    private void handleToken(String token, ChannelHandlerContext ctx) {
        // 获取token解析器
        TokenParser tokenParser = TokenParserHolder.get();
        if (tokenParser == null) {
            return;
        }

        // 解析token
        TokenParsed parsed = tokenParser.parse(token);
        if (parsed == null) {
            log.error("Token解析失败 ::: token={}", token);
            throw new BusinessException("Token解析失败");
        }

        // 设置accountId
        Long accountId = parsed.getAccountId();
        if (accountId != null) {
            ctx.channel().attr(AttributeKey.valueOf(ACCOUNT_ID)).set(IdUtils.toString(accountId));
        }

        // 设置额外参数
        Map<String, String> extraHeaders = parsed.getExtraHeaders();
        if (extraHeaders != null) {
            extraHeaders.forEach((k, v) -> {
                ctx.channel().attr(AttributeKey.valueOf(k)).set(v);
            });
        }
    }

    private String getToken(FullHttpRequest httpRequest) {
        String uri = httpRequest.uri();
        // /?token=xxx

        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        return decoder.parameters()
                .getOrDefault("token", List.of())
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * @param ip ip
     * @return value
     */
    private static Boolean isBlank(String ip) {
        return StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取租户ID
     *
     * @param channel 渠道
     * @return 租户ID
     */
    public static Long getTenantId(Channel channel) {
        Object tenantIdObj = channel.attr(AttributeKey.valueOf(TENANT_ID)).get();
        if (Objects.isNull(tenantIdObj)) {
            return null;
        }
        return Long.parseLong(tenantIdObj.toString());
    }
}
