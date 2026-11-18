package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;

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


    private String commandTopic = "ims.executor.command";
    /**
     * 执行器注册topic(执行器就绪后，注册到调度器)
     */
    private String registerTopic = "ims.executor.register";
    /**
     * 心跳topic（执行器启动后，周期性向调度器发送心跳，表示存活）
     */
    private String heartbeatTopic = "ims.executor.heartbeat";
    /**
     * 消息topic（执行器采集到的消息）
     */
    private String messageTopic = "ims.executor.message";
    /**
     * 任务topic（调度器发给执行器的任务，例如发送消息）
     */
    private String taskRequestTopic = "ims.executor.task.request";
    /**
     * 任务结果topic（执行器在执行任务过程中的反馈）
     */
    private String taskResponseTopic = "ims.executor.task.response";

}
