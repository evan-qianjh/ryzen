package com.qianjh.ryzen.transport;


import com.qianjh.ryzen.model.*;

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
    void register(ExecutorSnapshot snapshot);

    /**
     * 心跳
     *
     * @param snapshot 快照
     */
    void heartbeat(ExecutorSnapshot snapshot);

    /**
     *
     * 指令
     *
     * @param response 响应
     */
    void command(ExecutorCommandResponse response);

    /**
     * 启动
     *
     * @param snapshot 快照
     */
    void started(ExecutorSnapshot snapshot);

    /**
     * 停止
     *
     * @param snapshot 快照
     */
    void stopped(ExecutorSnapshot snapshot);

    /**
     * 任务受理
     *
     * @param task 任务
     */
    void taskAccepted(ExecutorTaskRequest task);

    /**
     * 任务执行中
     *
     * @param task 任务
     */
    void taskRunning(ExecutorTaskRequest task);

    /**
     * 任务完成
     *
     * @param result 结果
     */
    void taskFinished(ExecutorTaskResponse result);

    /**
     * 采集到的消息
     *
     * @param message 消息
     */
    void message(ExecutorMessage message);
}
