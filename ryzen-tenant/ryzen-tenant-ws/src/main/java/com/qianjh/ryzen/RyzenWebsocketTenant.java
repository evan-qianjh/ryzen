package com.qianjh.ryzen;

import com.qianjh.ryzen.framework.websocket.netty.Starter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

/**
 * @author QianJH
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RyzenWebsocketTenant {
    public static void main(String[] args) {
        SpringApplication.run(RyzenWebsocketTenant.class, args);

//        new Starter(8102, "/").start();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Starter webSocketStarter() {
        return new Starter(18002, "/");
    }
}
