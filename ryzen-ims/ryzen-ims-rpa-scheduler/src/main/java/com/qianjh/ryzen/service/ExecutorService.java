package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.model.ExecutorSnapshot;

import java.time.LocalDateTime;

public interface ExecutorService extends IService<Executor> {

    Executor getByCode(ImType type, String code);

    Executor getByAccount(ImType type, String accountCode);

    Executor create(ImType type, String code);

    Executor createIfAbsent(ImType type, String code);

    /**
     * 注册
     *
     * @param executor 执行器
     * @param snapshot 快照
     * @return 是否成功
     */
    boolean register(Executor executor, ExecutorSnapshot snapshot);

    /**
     * 心跳
     *
     * @param executor 执行器
     * @param snapshot 快照
     * @return 是否成功
     */
    boolean heartbeat(Executor executor, ExecutorSnapshot snapshot);

    /**
     * 下线
     *
     * @param executor 执行器
     * @param cutoff
     * @return 是否成功
     */
    boolean offline(Executor executor, LocalDateTime cutoff);

    /**
     * 标记执行器可以执行新的任务
     *
     * @param executorId     执行器ID
     * @param executorTaskId 执行器任务ID
     * @return 是否成功
     */
    boolean markExecutorReadyAfterTask(Long executorId, Long executorTaskId);
}
