package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ryzen.scheduler")
public class SchedulerProperties {
    /**
     * 命令topic
     */
    private String commandRequestTopic = "ims.app-to-scheduler-command-request";
    private String commandResponseTopic = "ims.scheduler-to-app-command-response";
    /**
     * 任务topic
     */
    private String taskRequestTopic = "ims.app-to-scheduler-task-request";
    private String taskResponseTopic = "ims.scheduler-to-app-task-response";
    /**
     * 聚合消息topic
     */
    private String messageTopic = "ims.scheduler-to-app-message";
    private String reportTopic = "ims.scheduler-to-app-report";

    /**
     * 心跳超时时间
     */
    private long heartbeatTimeoutSeconds = 30;
    /**
     * 任务执行超时时间
     */
    private long taskTimeoutSeconds = 60;
    /**
     * 超时检测间隔
     */
    private long timeoutCheckIntervalSeconds = 5;
}
