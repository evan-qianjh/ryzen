package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ryzen.scheduler")
public class SchedulerProperties {
    private String executorCommandRequestTopic = "ims.executor.command.request";
    private String executorCommandResponseTopic = "ims.executor.command.response";
    /**
     * 执行器任务topic
     */
    private String executorTaskRequestTopic = "ims.executor.send.request";
    /**
     * 执行器结果topic
     */
    private String executorTaskResponseTopic = "ims.executor.send.response";
    /**
     * 执行器消息topic
     */
    private String executorMessageTopic = "ims.executor.message";
    /**
     * 执行器注册topic
     */
    private String executorRegisterTopic = "ims.executor.register";
    /**
     * 执行器心跳topic
     */
    private String executorHeartbeatTopic = "ims.executor.heartbeat";
    /**
     * 执行期状态topic
     */
    private String executorStatusTopic = "ims.executor.status";


    /**
     * 命令topic
     */
    private String commandRequestTopic = "ims.scheduler.command.request";
    private String commandResponseTopic = "ims.scheduler.command.response";
    /**
     * 任务topic
     */
    private String taskRequestTopic = "ims.scheduler.task.request";
    private String taskResponseTopic = "ims.scheduler.task.response";
    /**
     * 聚合消息topic
     */
    private String messageTopic = "ims.scheduler.message";

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
