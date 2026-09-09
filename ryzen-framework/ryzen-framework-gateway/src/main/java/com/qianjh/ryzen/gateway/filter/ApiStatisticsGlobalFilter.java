package com.qianjh.ryzen.gateway.filter;

import com.qianjh.ryzen.gateway.model.Api;
import com.qianjh.ryzen.gateway.model.ApiCounter;
import com.qianjh.ryzen.gateway.model.ApiStatistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author QianJH
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiStatisticsGlobalFilter implements GlobalFilter, Ordered {
    private static final Logger API_STATISTICS_LOGGER = LoggerFactory.getLogger("API_STATISTICS_LOGGER");

    public static final Integer ORDER = Integer.MIN_VALUE;

    private static final String REPLACE_PATH = "{";
    private static final String MATCH_PATH = "*";


    private static final int MAX_COUNTER = 1000;
    private final Map<String, ApiCounter> COUNTERS = new ConcurrentHashMap<>();

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.application.name}:${spring.cloud.client.ip-address}:${server.port}")
    private String instanceName;

//    private final ApiStatisticsPublisher apiStatisticsPublisher;

    private static final String REQUEST_TIME = "api_request_time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 超出限制了, 防止OOM
        if (COUNTERS.size() >= MAX_COUNTER) {
            log.error("超出限制 ::: max={}", MAX_COUNTER);
            return chain.filter(exchange);
        }

        // 获取请求
        final String apiName = apiName(exchange);


        // 初始化统计
        ApiCounter counter = COUNTERS.get(apiName);
        if (counter == null) {
            Api api = api(exchange);
            counter = new ApiCounter(applicationName, instanceName, api);
            COUNTERS.put(apiName, counter);
        }

        // 请求
        counter.request();


        exchange.getAttributes().put(REQUEST_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long requestTime = exchange.getAttribute(REQUEST_TIME);
            if (Objects.isNull(requestTime)) {
                log.warn("{} is null", REQUEST_TIME);
                return;
            }

            // 执行时间
            long ms = System.currentTimeMillis() - requestTime;

            ApiCounter end = COUNTERS.get(apiName);
            // 响应
            if (end == null) {
                log.error("计数器并发移除丢失 ::: apiName={}", apiName);
            } else {
                end.response(ms);
            }
        }));
    }

    /**
     * 解析出请求
     *
     * @param exchange exchange
     * @return 请求
     */
    private Api api(ServerWebExchange exchange) {
        String method = exchange.getRequest().getMethod().name();
        String path = getPath(exchange);
        return Api.builder()
                .method(method)
                .path(path)
                .build();
    }

    private String apiName(ServerWebExchange exchange) {
        String method = exchange.getRequest().getMethod().name();
        String path = getPath(exchange);

        return method + " " + path;
    }


    /**
     * 获取path
     *
     * @param exchange exchange
     * @return path
     */
    private String getPath(ServerWebExchange exchange) {
//        // 断言匹配的PATH
//        Object predicateMatchedPathObj = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ATTR);
//        if (Objects.isNull(predicateMatchedPathObj)) {
//            return null;
//        }
//        String predicateMatchedPath = predicateMatchedPathObj.toString();
//
//        // 宏替换类 @PathVariable
//        // e.g. /v4/order/{orderId}
//        if (predicateMatchedPath.contains(REPLACE_PATH)) {
//            return predicateMatchedPath;
//        }
//
//        // 模糊匹配类
//        // e.g. /v4/order/**  可能包含宏替换的  /v4/order/123
//        if (predicateMatchedPath.contains(MATCH_PATH)) {
//            // TODO 对于模糊匹配类中出现宏替换方式，如何提取出宏?
//
//            return predicateMatchedPath;
//        }
//
//        // 精准的
//        return predicateMatchedPath;

        return exchange.getRequest().getURI().getRawPath();
    }

    /**
     * 同步
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sync() {
        Long timestamp = System.currentTimeMillis() / 1000L;

        List<ApiStatistics> items = new ArrayList<>();

        //
        COUNTERS.forEach((api, counter) -> {
            ApiStatistics statistics = counter.statistics(timestamp);

            // 没有请求，丢弃
            if (statistics.getRequest() == 0L) {
                return;
            }

            API_STATISTICS_LOGGER.info("{}", statistics);
            items.add(statistics);
        });


        try {
            // TODO
//            items.forEach(apiStatisticsPublisher::publish);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 清空
        COUNTERS.clear();
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
