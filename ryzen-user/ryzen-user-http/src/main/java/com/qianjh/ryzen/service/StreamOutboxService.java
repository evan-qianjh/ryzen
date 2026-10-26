package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.StreamOutbox;

import java.util.List;

/**
 * @author QianJH
 */
public interface StreamOutboxService extends IService<StreamOutbox> {

    /**
     * 创建
     *
     * @param topic       主题
     * @param shardingKey 分片KEY
     * @param domain      领域
     * @param type        类型
     * @param body        报文体
     * @param <T>         报文体泛型
     * @return entity
     */
    <T> StreamOutbox create(String topic, String shardingKey, String domain, String type, T body);

    /**
     * 提取
     *
     * @return 实体
     */
    List<StreamOutbox> claims();

    /**
     * 移除
     *
     * @param entity 实体
     */
    void remove(StreamOutbox entity);

    /**
     * 重试
     *
     * @param entity 实体
     */
    void retry(StreamOutbox entity);
}
