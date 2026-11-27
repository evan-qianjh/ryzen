package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

/**
 * 执行器配置文件
 *
 * @author QianJH
 */
@Data
@ConfigurationProperties(prefix = "ryzen.executor")
public class ExecutorProperties {
    /**
     * 执行器
     */
    private String code = "001";
    /**
     * 是否不展示可视化
     */
    private boolean headless = false;
    /**
     * 浏览器选择
     */
    private String browserChannel = "chrome";
    /**
     * 浏览器 Profile 根目录
     */
    private Path profileRoot = Path.of("./browser-data/profiles");

    /**
     * 采集间隔
     */
    private Long collectIntervalSeconds = 5L;
    /**
     * 心跳间隔
     */
    private Long heartbeatIntervalSeconds = 10L;
    /**
     * 登录检测间隔
     */
    private Long loginCheckIntervalSeconds = 2L;


    private String executorToSchedulerTopic = "ims.executor-to-scheduler";
    private String schedulerToExecutorTopic = "ims.scheduler-to-executor";

}
