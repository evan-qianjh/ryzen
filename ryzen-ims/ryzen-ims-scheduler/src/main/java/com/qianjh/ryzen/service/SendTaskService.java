package com.qianjh.ryzen.service;

import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.model.ExecutorTaskResponse;
import com.qianjh.ryzen.model.SchedulerRequest;

public interface SendTaskService {

    /**
     * 受理请求
     *
     * @param request 请求
     */
    void accept(SchedulerRequest request);

    /**
     * 调度任务请求
     *
     * @param imType      IM类型
     * @param accountCode 账户
     */
    void dispatchTaskRequest(ImType imType, String accountCode);

    /**
     * 处理任务结果
     *
     * @param response 结果
     */
    void handleTaskResponse(ExecutorTaskResponse response);
}
