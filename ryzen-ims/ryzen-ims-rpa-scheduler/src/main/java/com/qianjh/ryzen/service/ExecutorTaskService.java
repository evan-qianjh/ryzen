package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;
import com.qianjh.ryzen.enums.ExecutorTaskStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.model.ExecutorTaskResponse;

import java.time.LocalDateTime;

public interface ExecutorTaskService extends IService<ExecutorTask> {

    ExecutorTask getByUk(String commandCode);

    ExecutorTask getByCommandCode(String commandCode);

    ExecutorTask create(String commandCode, Long assignmentId, String conversationCode, String content, LocalDateTime deadlineTime);

    ExecutorTask createIfAbsent(String commandCode, Long assignmentId, String conversationCode, String content, LocalDateTime deadlineTime);

    /**
     * 待执行任务标记超时
     *
     * @param now 当前时间
     * @return 结果
     */
    boolean timeoutPendingTasks(LocalDateTime now);

    /**
     * 进行中任务标记未知
     *
     * @param now 当前时间
     * @return 结果
     */
    boolean unknownRunningTasks(LocalDateTime now);

    /**
     * 标记调度结果未知
     *
     * @param entity  任务
     * @param message 错误消息
     * @return 结果
     */
    boolean markDispatchUnknown(ExecutorTask entity, String message);

    /**
     * 更新任务执行结果
     *
     * @param executor 执行器
     * @param event    结果
     * @param status   状态
     * @return 结果
     */
    boolean updateTaskEvent(Executor executor, ExecutorTaskResponse event, ExecutorTaskStatus status);
}
