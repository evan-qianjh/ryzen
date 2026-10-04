package com.qianjh.ryzen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author QianJH
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RyzenServiceStream {
    static void main(String[] args) {
        SpringApplication.run(RyzenServiceStream.class, args);
    }
}
