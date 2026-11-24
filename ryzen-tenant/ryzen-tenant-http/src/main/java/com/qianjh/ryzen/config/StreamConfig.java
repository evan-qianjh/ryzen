package com.qianjh.ryzen.config;

import com.qianjh.ryzen.framework.stream.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StreamConfig {

    @Value("${spring.application.name}")
    private String name;
    @Value("${spring.cloud.client.ip-address}:${server.port}")
    private String ipPort;

    @Bean
    public Producer getProducer() {
        String instance = String.format("%s:%s", name, ipPort);
        Producer producer = Producer.builder()
                .name(name)
                .instance(instance)
                .build();

        log.info("Created Producer: {}", instance);
        return producer;
    }
}
