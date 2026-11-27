package com.qianjh.ryzen.service;

import com.qianjh.ryzen.ims.stream.executor_to_scheduler.report.ExecutorToSchedulerCommandResponse;

public interface ExecutorCommandService {
    void handle(ExecutorToSchedulerCommandResponse.Body body);
}
