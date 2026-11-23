package com.qianjh.ryzen;

import com.qianjh.ryzen.tenant.config.StreamDefaultBeanConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @author QianJH
 */
@Import(StreamDefaultBeanConfig.class)
@EnableDiscoveryClient
@SpringBootApplication
public class RyzenImsStream {
    static void main(String[] args) {
        SpringApplication.run(RyzenImsStream.class, args);
    }
}
