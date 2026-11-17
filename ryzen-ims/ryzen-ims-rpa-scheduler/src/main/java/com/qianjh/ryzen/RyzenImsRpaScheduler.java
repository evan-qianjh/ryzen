package com.qianjh.ryzen;

import com.qianjh.ryzen.config.SchedulerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author QianJH
 */
@EnableDiscoveryClient
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(SchedulerProperties.class)
public class RyzenImsRpaScheduler {

    static void main(String[] args) {
        SpringApplication.run(RyzenImsRpaScheduler.class, args);
    }
}
