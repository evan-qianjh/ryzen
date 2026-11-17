package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorTask;

public interface TaskDispatchService {
    ExecutorTask claimNextTask(Executor executor);
}
