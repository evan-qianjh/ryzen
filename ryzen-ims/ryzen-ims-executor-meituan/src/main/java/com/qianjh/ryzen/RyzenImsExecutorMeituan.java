package com.qianjh.ryzen;

import com.qianjh.ryzen.config.ExecutorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author QianJH
 */
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(ExecutorProperties.class)
public class RyzenImsExecutorMeituan {
    static void main(String[] args) {
        SpringApplication.run(RyzenImsExecutorMeituan.class, args);
    }
}
