package com.qianjh.ryzen;

import com.qianjh.ryzen.framework.websocket.netty.Starter;
import com.qianjh.ryzen.tenant.config.StreamDefaultBeanConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author QianJH
 */
@Import(StreamDefaultBeanConfig.class)
@EnableDiscoveryClient
@SpringBootApplication
public class RyzenTenantWs {
    static void main(String[] args) {
        SpringApplication.run(RyzenTenantWs.class, args);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Starter webSocketStarter() {
        return new Starter(18002, "/");
    }
}
