package com.qianjh.ryzen.transport;


import com.qianjh.ryzen.ims.dto.ExecutorMessage;
import com.qianjh.ryzen.ims.dto.ExecutorSnapshot;
import com.qianjh.ryzen.ims.enums.ExecutorToSchedulerReportEventType;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerCommandResponse;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerTaskResponse;

/**
 * 执行器通信层
 *
 * @author QianJH
 */
public interface ExecutorTransport {
    /**
     * 注册
     *
     * @param snapshot 快照
     */
    void reportRegister(ExecutorSnapshot snapshot);

    /**
     * 心跳
     *
     * @param snapshot 快照
     */
    void reportHeartbeat(ExecutorSnapshot snapshot);

    /**
     * 状态
     *
     * @param snapshot 快照
     */
    void reportEvent(ExecutorSnapshot snapshot, ExecutorToSchedulerReportEventType type);

    /**
     * 采集到的消息
     *
     * @param message 消息
     */
    void reportMessage(ExecutorMessage message);

    /**
     *
     * 指令响应
     *
     * @param response 响应
     */
    void commandResponse(ExecutorToSchedulerCommandResponse.Body response);

    /**
     * 任务响应
     *
     * @param result 结果
     */
    void taskResponse(ExecutorToSchedulerTaskResponse.Body result);

}
