package com.qianjh.ryzen.service;

import com.qianjh.ryzen.model.ExecutorTaskResponse;
import com.qianjh.ryzen.model.SchedulerTaskRequest;

public interface SchedulerTaskService {

    /**
     * 受理请求
     *
     * @param request 请求
     */
    void accept(SchedulerTaskRequest request);

    /**
     * 调度任务请求
     *
     * @param imType      IM类型
     * @param accountCode 账户
     */
    void dispatchTaskRequest(Long assignmentId);

    /**
     * 处理任务结果
     *
     * @param response 结果
     */
    void handleTaskResponse(ExecutorTaskResponse response);
}
