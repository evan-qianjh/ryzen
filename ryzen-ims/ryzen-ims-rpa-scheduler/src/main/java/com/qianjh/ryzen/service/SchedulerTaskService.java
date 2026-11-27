package com.qianjh.ryzen.service;

import com.qianjh.ryzen.ims.stream.app_to_scheduler.task.AppToSchedulerTaskRequest;
import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerTaskResponse;

public interface SchedulerTaskService {

    /**
     * 受理请求
     *
     * @param request 请求
     */
    void accept(AppToSchedulerTaskRequest.Body request);

    /**
     * 调度任务请求
     *
     * @param assignmentId 分配
     */
    void dispatchTaskRequest(Long assignmentId);

    /**
     * 处理任务结果
     *
     * @param response 结果
     */
    void handleTaskResponse(ExecutorToSchedulerTaskResponse.Body body);
}
