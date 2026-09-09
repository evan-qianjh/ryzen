package com.qianjh.ryzen.gateway.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author QianJH
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ApiStatistics implements Serializable {
    /**
     * 服务名
     */
    private String service;
    /**
     * 服务实例
     */
    private String instance;


    /**
     * 方法
     */
    private String method;
    /**
     * 路径
     */
    private String path;



    /**
     * 时间戳
     */
    private Long timestamp;



    /**
     * 请求次数
     */
    private Long request;
    /**
     * 响应次数
     */
    private Long response;
    /**
     * 总计耗时
     */
    private Long sumMs;
    /**
     * 最大耗时
     */
    private Long maxMs;
    /**
     * 最小耗时
     */
    private Long minMs;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
