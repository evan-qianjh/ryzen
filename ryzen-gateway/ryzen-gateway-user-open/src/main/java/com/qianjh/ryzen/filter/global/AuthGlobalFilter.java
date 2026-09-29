package com.qianjh.ryzen.filter.global;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qianjh.ryzen.user.entity.Apikey;
import com.qianjh.ryzen.filter.dto.GwMc;
import com.qianjh.ryzen.framework.common.header.GatewayHeaderUser;
import com.qianjh.ryzen.framework.gateway.filter.ForgedRequestGlobalFilter;
import com.qianjh.ryzen.framework.security.util.RSAUtils;
import com.qianjh.ryzen.mapper.ApikeyMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * @author QianJH
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    public static final Integer ORDER = Math.max(ForgedRequestGlobalFilter.ORDER, OemGlobalFilter.ORDER) + 1;

    /**
     * 标准请求头
     */
    public static final String REQUEST_HEADER_ALGORITHM = "x-algorithm";
    public static final String REQUEST_HEADER_API_KEY = "x-apikey";
    public static final String REQUEST_HEADER_RECV_WINDOW = "x-recvwindow";
    public static final String REQUEST_HEADER_TIMESTAMP = "x-timestamp";
    public static final String REQUEST_HEADER_SIGNATURE = "x-signature";

    private static final Long MIN_RECV_WINDOW = 5_000L;
    private static final Long MAX_RECV_WINDOW = 300_000L;

    private final static Pattern AUTH_PATH_PATTERN = Pattern.compile("^/[a-zA-Z0-9_-]+/public/");

    /**
     * 参与签名的头
     */
    private static final String[] SIGN_HEADERS = new String[]{
            REQUEST_HEADER_ALGORITHM,
            REQUEST_HEADER_API_KEY,
            REQUEST_HEADER_RECV_WINDOW,
            REQUEST_HEADER_TIMESTAMP
    };

    private final ApikeyMapper apikeyMapper;

    private Apikey getApiKey(Long oemId, String keyId) {
        return apikeyMapper.selectOne(new LambdaQueryWrapper<Apikey>()
                .eq(Apikey::getOemId, oemId)
                .eq(Apikey::getApikey, keyId)
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod method = request.getMethod();
        HttpHeaders headers = request.getHeaders();
        RequestPath path = request.getPath();

        // 无需鉴权的 ： /{service}/public/xxx
        boolean publicPath = AUTH_PATH_PATTERN.matcher(path.value()).find();
        if (publicPath) {
            return chain.filter(exchange);
        }

        // 校验有效时间
        validRecvWindow(request);

        // 获取APIkey
        String keyId = getHeaderVal(headers, REQUEST_HEADER_API_KEY);
        Assert.isTrue(StringUtils.hasText(keyId), GwMc.AUTH_001.name());
        Long oemId = getHeaderLongVal(headers, GatewayHeaderUser.OEM_ID);
        Apikey apiKey = getApiKey(oemId, keyId);
        Assert.notNull(apiKey, GwMc.AUTH_101.name());

        // 校验状态
        Assert.isTrue(apiKey.getEnabled(), GwMc.AUTH_101.name());

        // 校验绑定IP
        validBindIp(apiKey, request);

        // 请求方法是否包含body
        boolean hasBody = method == HttpMethod.POST
                || method == HttpMethod.PUT
                || method == HttpMethod.PATCH;

        // 没有body
        if (!hasBody) {
            return validSign(apiKey, exchange, chain, null);
        }

        // 有body
        return DataBufferUtils.join(request.getBody())
                .flatMap(dataBuffer -> {
                    // 读body
                    byte[] bodyBytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bodyBytes);
                    DataBufferUtils.release(dataBuffer);

                    String bodyStr = new String(bodyBytes, StandardCharsets.UTF_8);

                    // 重新包装 request body，保证下游可读
                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bodyBytes);
                        return Mono.just(buffer);
                    });

                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(mutatedRequest)
                            .build();

                    return validSign(apiKey, mutatedExchange, chain, bodyStr);
                });
    }

    /**
     * 获取请求头值
     *
     * @param headers 请求header集合
     * @param header  目标header
     * @return header value
     */
    public static String getHeaderVal(HttpHeaders headers, String header) {
        return headers.getFirst(header);
    }

    public static Long getHeaderLongVal(HttpHeaders headers, String header) {
        String headerVal = getHeaderVal(headers, header);
        if(headerVal == null) {
            return null;
        }
        return Long.parseLong(headerVal);
    }

    /**
     * 校验请求有效时间
     *
     * @param request 请求
     */
    private void validRecvWindow(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        // 请求有效时间
        String windowLimitStr = getHeaderVal(headers, REQUEST_HEADER_RECV_WINDOW);
        Assert.isTrue(StringUtils.hasText(windowLimitStr), GwMc.AUTH_003.name());
        long windowLimit = Long.parseLong(windowLimitStr);
        Assert.isTrue(windowLimit >= MIN_RECV_WINDOW && windowLimit <= MAX_RECV_WINDOW, GwMc.AUTH_004.name());


        // 客户端时间
        String timestampStr = getHeaderVal(headers, REQUEST_HEADER_TIMESTAMP);
        Assert.isTrue(StringUtils.hasText(timestampStr), GwMc.AUTH_002.name());
        long requestTimestamp = Long.parseLong(timestampStr);


        // 比较请求报文是否超出有效时间窗口
        long currentTimestamp = System.currentTimeMillis();
        long windowDiff = Math.abs(currentTimestamp - requestTimestamp);
        log.debug("requestTimestamp={}, currentTimestamp={}, windowLimit={}, windowDiff={}", requestTimestamp, currentTimestamp, windowLimit, windowDiff);
        Assert.isTrue(windowDiff < windowLimit, GwMc.AUTH_105.name());
    }


    /**
     * 校验绑定IP
     *
     * @param apiKey  apikey
     * @param request 请求
     */
    private void validBindIp(Apikey apiKey, ServerHttpRequest request) {
        // TODO
    }


    /**
     * 校验签名
     *
     * @param apiKey   apikey
     * @param exchange 请求
     * @param chain    chain
     * @param body     body
     */
    private Mono<Void> validSign(Apikey apiKey, ServerWebExchange exchange, GatewayFilterChain chain, String body) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        // 算法
        String algorithm = getSignAlgorithm(headers);
        Assert.notNull(algorithm, GwMc.AUTH_006.name());

        // 请求的签名
        String signature = getHeaderVal(headers, REQUEST_HEADER_SIGNATURE);
        Assert.isTrue(StringUtils.hasText(signature), GwMc.AUTH_007.name());

        // 构建签名内容
        String original = buildSignOriginal(exchange, body);

        // 验签
        RSAPublicKey publicKey = RSAUtils.buildRsaPublicKey(apiKey.getPublicKey());
        Boolean signValid = null;
        try {
            signValid = RSAUtils.signVerify(publicKey, algorithm, original, signature);
        } catch (Exception e) {
            log.error("验签失败 ::: {}", e.getMessage(), e);
        }
        // 验证签名
        Assert.isTrue(signValid != null && signValid, GwMc.AUTH_103.name());

        //
        ServerHttpRequest nextRequest = request.mutate()
                .header(GatewayHeaderUser.TENANT_ID, String.valueOf(apiKey.getTenantId()))
                .header(GatewayHeaderUser.ACCOUNT_ID, String.valueOf(apiKey.getAccountId()))
                .build();

        return chain.filter(exchange.mutate().request(nextRequest).build());
    }

    /**
     * 构建签名明文
     * header#path#query#body
     *
     * @param exchange 请求
     * @param body     body
     * @return 签名明文
     */
    private String buildSignOriginal(ServerWebExchange exchange, String body) {
        StringBuilder builder = new StringBuilder();

        // header
        String header = getSignHeader(exchange);
        builder.append(header);

        // method
        String method = getSignMethod(exchange);
        builder.append("#").append(method);

        // path
        String path = getSignPath(exchange);
        builder.append("#").append(path);

        // query
        String queryParams = getSignQueryParams(exchange);
        if (StringUtils.hasText(queryParams)) {
            builder.append("#").append(queryParams);
        }

        // body
        if (StringUtils.hasText(body)) {
            builder.append("#").append(body);
        }

        String original = builder.toString();
        if (log.isDebugEnabled()) {
            log.debug("buildSignOriginal ::: {}", original);
        }

        return original;
    }


    /**
     * 获取签名请求头
     *
     * @param exchange 请求
     * @return 签名头
     */
    private String getSignHeader(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();


        StringBuilder builder = new StringBuilder();
        for (String signHeader : SIGN_HEADERS) {
            builder.append(signHeader).append("=").append(headers.getFirst(signHeader)).append("&");
        }
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    /**
     * 获取签名请求方法
     *
     * @param exchange 请求
     * @return 签名请求方法
     */
    private String getSignMethod(ServerWebExchange exchange) {
        return exchange.getRequest().getMethod().name().toUpperCase();
    }

    /**
     * 获取签名请求路径
     *
     * @param exchange 请求
     * @return 签名请求路径
     */
    private String getSignPath(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return request.getPath().value();
    }

    /**
     * 获取签名请求查询参数
     *
     * @param exchange 请求
     * @return 签名请求查询参数
     */
    private String getSignQueryParams(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        TreeMap<String, String> map = new TreeMap<>(queryParams.toSingleValueMap());


        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }


    /**
     * 获取算法
     *
     * @param headers 请求头
     * @return 算法
     */
    private String getSignAlgorithm(HttpHeaders headers) {
        // 校验必填
        String algorithm = getHeaderVal(headers, REQUEST_HEADER_ALGORITHM);
        Assert.isTrue(StringUtils.hasText(algorithm), GwMc.AUTH_005.name());

        // 校验正确性(目前仅支持SHA256WithRSA)
        Assert.isTrue(RSAUtils.ALGORITHM_SHA256WithRSA.equals(algorithm), GwMc.AUTH_006.name());

        return algorithm;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}