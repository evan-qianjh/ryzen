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
    private String executorCode = "ryzen-ims-executor-meituan-001";
    /**
     * 目标平台账号
     */
    private String accountCode = "123";
    /**
     * 平台地址
     */
    private String platformUrl = "https://g.dianping.com/srimworkbench/home/index.html?appId=104&clientType=100900#/im/page/workbench/reception";
    /**
     * 浏览器选择
     */
    private String browserChannel = "chrome";
    /**
     * 是否不展示可视化
     */
    private boolean headless = false;
    /**
     * 会话持久化目录
     */
    private Path userDataDir = Path.of("./browser-data/meituan/" + accountCode);
    /**
     * 采集间隔
     */
    private Duration collectInterval = Duration.ofSeconds(5);
    /**
     * 心跳间隔
     */
    private Duration heartbeatInterval = Duration.ofSeconds(10);
    /**
     * 登录检测间隔
     */
    private Duration loginCheckInterval = Duration.ofSeconds(2);
    /**
     * 执行器注册topic(执行器就绪后，注册到调度器)
     */
    private String registerTopic = "chat.executor.register";
    /**
     * 心跳topic（执行器启动后，周期性向调度器发送心跳，表示存活）
     */
    private String heartbeatTopic = "chat.executor.heartbeat";
    /**
     * 任务topic（调度器发给执行器的任务，例如发送消息）
     */
    private String taskRequestTopic = "chat.executor.task.request";
    /**
     * 任务结果topic（执行器在执行任务过程中的反馈）
     */
    private String taskResponseTopic = "chat.executor.task.response";
    /**
     * 消息topic（执行器采集到的消息）
     */
    private String messageTopic = "chat.executor.message";
    /**
     * 登录成功DOM选择器
     */
    private String loginSuccessSelector = "";

}
