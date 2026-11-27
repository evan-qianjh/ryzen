package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.ims.dto.ExecutorSnapshot;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportHeartbeat;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerReportRegister;

import java.time.LocalDateTime;

public interface ExecutorService extends IService<Executor> {

    Executor getByCode(String code);

    Executor getByAssignment(Long assignmentId);

    Executor create(String code);

    Executor createIfAbsent(String code);

    /**
     * 注册
     *
     * @param executor 执行器
     * @param snapshot 快照
     * @return 是否成功
     */
    boolean register(Executor executor, ExecutorToSchedulerReportRegister.Body body);

    /**
     * 心跳
     *
     * @param executor 执行器
     * @param snapshot 快照
     * @return 是否成功
     */
    boolean heartbeat(Executor executor, ExecutorToSchedulerReportHeartbeat.Body body, long timestamp);

    /**
     * 下线
     *
     * @param executor 执行器
     * @param cutoff
     * @return 是否成功
     */
    boolean offline(Executor executor, LocalDateTime cutoff);

    /**
     * 分配
     *
     * @param executor   执行器
     * @param assignment 分配单
     * @return 结果
     */
    boolean assign(Executor executor, ExecutorAssignment assignment);

    /**
     * 释放
     *
     * @param executor   执行器
     * @param assignment 分配单
     * @return 结果
     */
    boolean release(Executor executor, ExecutorAssignment assignment);

    /**
     * 标记执行器可以执行新的任务
     *
     * @param executorId     执行器ID
     * @param executorTaskId 执行器任务ID
     * @return 是否成功
     */
    boolean markExecutorReadyAfterTask(Long executorId, Long executorTaskId);
}
