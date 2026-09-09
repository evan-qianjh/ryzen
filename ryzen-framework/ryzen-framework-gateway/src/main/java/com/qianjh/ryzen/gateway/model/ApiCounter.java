package com.qianjh.ryzen.gateway.model;

import com.qianjh.ryzen.util.GsonUtils;
import lombok.Getter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author QianJH
 */
@Getter
public class ApiCounter implements Serializable {
    private final String service;
    private final String instance;
    private final Api api;

    public ApiCounter(String service, String instance, Api api) {
        this.service = service;
        this.instance = instance;
        this.api = api;
    }

    private final AtomicLong request = new AtomicLong(0L);
    private final AtomicLong response = new AtomicLong(0L);
    private final AtomicLong sumMs = new AtomicLong(0L);
    private final AtomicLong minMs = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxMs = new AtomicLong(Long.MIN_VALUE);

    /**
     * 请求
     */
    public void request() {
        request.incrementAndGet();
    }

    /**
     * 响应
     *
     * @param ms 用时
     */
    public void response(Long ms) {
        response.incrementAndGet();
        sumMs.getAndAdd(ms);
        minMs.set(Math.min(minMs.get(), ms));
        maxMs.set(Math.max(maxMs.get(), ms));
    }

    /**
     * 统计
     *
     * @param timestamp 统计时间
     * @return 结果
     */
    public ApiStatistics statistics(Long timestamp) {

        Long min = minMs.getAndSet(Long.MAX_VALUE);
        if (min == Long.MAX_VALUE) {
            min = null;
        }

        Long max = maxMs.getAndSet(Long.MIN_VALUE);
        if (max == Long.MIN_VALUE) {
            max = null;
        }

        return ApiStatistics.builder()
                .service(service)
                .instance(instance)

                .method(api.getMethod())
                .path(api.getPath())

                .timestamp(timestamp)

                .request(request.getAndSet(0L))
                .response(response.getAndSet(0L))
                .sumMs(sumMs.getAndSet(0L))
                .minMs(min)
                .maxMs(max)
                .build();
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
