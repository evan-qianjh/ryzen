package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ryzen.executor")
public class ExecutorProperties {
    private String commandRequestTopic = "ims.scheduler-to-executor-command-request";
    private String commandResponseTopic = "ims.executor-to-scheduler-command-response";
    /**
     * 执行器任务topic
     */
    private String taskRequestTopic = "ims.scheduler-to-executor-task-request";
    /**
     * 执行器结果topic
     */
    private String taskResponseTopic = "ims.executor-to-scheduler-task-response";
    /**
     * 执行器消息topic
     */
    private String messageTopic = "ims.executor-to-scheduler-message";
    /**
     * 执行器注册topic
     */
    private String registerTopic = "ims.executor-to-scheduler-register";
    /**
     * 执行器心跳topic
     */
    private String heartbeatTopic = "ims.executor-to-scheduler-heartbeat";
    /**
     * 执行期状态topic
     */
    private String statusTopic = "ims.executor-to-scheduler-status";
}
