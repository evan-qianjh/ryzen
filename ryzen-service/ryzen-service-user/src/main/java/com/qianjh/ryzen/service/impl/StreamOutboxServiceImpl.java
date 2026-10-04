package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.StreamOutbox;
import com.qianjh.ryzen.framework.common.util.GsonUtils;
import com.qianjh.ryzen.framework.stream.model.StreamPayload;
import com.qianjh.ryzen.framework.stream.producer.Producer;
import com.qianjh.ryzen.mapper.StreamOutboxMapper;
import com.qianjh.ryzen.service.StreamOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamOutboxServiceImpl extends ServiceImpl<StreamOutboxMapper, StreamOutbox> implements StreamOutboxService {

    private final Producer producer;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public <T> StreamOutbox create(String topic, String shardingKey, String domain, String type, T body) {
        StreamPayload<T> payload = StreamPayload.<T>builder()
                .domain(domain)
                .type(type)
                .body(body).build();
        String payloadString = GsonUtils.toJson(payload);

        StreamOutbox entity = StreamOutbox.builder()
                .topic(topic)
                .shardingKey(shardingKey)
                .domain(domain)
                .type(type)
                .payload(payloadString)
                .producer(producer.getName())
                .producerInstance(producer.getInstance())
                .lockedBy(null)
                .lockedTime(null)
                .retryCount(null)
                .nextRetryTime(null)
                .build();
        save(entity);

        log.info("发布事件 ::: id={}, topic={}, domain={}, type={}, payload={}",
                entity.getId(), topic, domain, type, payloadString
        );

        return entity;
    }
}
