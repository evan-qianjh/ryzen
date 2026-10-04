package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.StreamOutbox;

/**
 * @author QianJH
 */
public interface StreamOutboxService extends IService<StreamOutbox> {

    <T> StreamOutbox create(String topic, String shardingKey, String domain, String type, T body);
}
