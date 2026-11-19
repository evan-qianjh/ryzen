package com.qianjh.ryzen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ryzen.executor")
public class ExecutorProperties {
    private String commandRequestTopic = "ims.executor.command.request";
    private String commandResponseTopic = "ims.executor.command.response";
    /**
     * 执行器任务topic
     */
    private String taskRequestTopic = "ims.executor.send.request";
    /**
     * 执行器结果topic
     */
    private String taskResponseTopic = "ims.executor.send.response";
    /**
     * 执行器消息topic
     */
    private String messageTopic = "ims.executor.message";
    /**
     * 执行器注册topic
     */
    private String registerTopic = "ims.executor.register";
    /**
     * 执行器心跳topic
     */
    private String heartbeatTopic = "ims.executor.heartbeat";
    /**
     * 执行期状态topic
     */
    private String statusTopic = "ims.executor.status";
}
