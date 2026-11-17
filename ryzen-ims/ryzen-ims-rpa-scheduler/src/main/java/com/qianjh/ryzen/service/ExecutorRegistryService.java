package com.qianjh.ryzen.service;

import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.model.ExecutorSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorRegistryService {
    private final SendTaskService sendTaskService;
    private final ExecutorService executorService;

    public void register(ExecutorSnapshot snapshot) {
        validate(snapshot);
        try {
            // 如果不存在则创建执行器
            Executor executor = executorService.createIfAbsent(snapshot.imType(), snapshot.executorCode());

            // 注册
            executorService.register(executor, snapshot);
        } catch (DuplicateKeyException e) {
            // account_id 唯一：防止同一个平台账号同时被两个执行器接管。
            throw new IllegalStateException("Account is already bound to another executor: " + snapshot.accountCode(), e);
        }
        log.info("Executor registered: executorCode={}, accountCode={}, status={}",
                snapshot.executorCode(), snapshot.accountCode(), snapshot.status());

        // 如果就绪，调度任务请求
        if (snapshot.status() == ExecutorStatus.READY) {
            sendTaskService.dispatchTaskRequest(snapshot.imType(), snapshot.accountCode());
        }
    }

    public void heartbeat(ExecutorSnapshot snapshot) {
        validate(snapshot);
        Executor executor = executorService.getByCode(snapshot.imType(), snapshot.executorCode());
        if (executor == null) {
            log.warn("Heartbeat received from unregistered executor: {}", snapshot.executorCode());
            return;
        }
        boolean success = executorService.heartbeat(executor, snapshot);
        if (snapshot.status() == ExecutorStatus.READY) {
            sendTaskService.dispatchTaskRequest(snapshot.imType(), snapshot.accountCode());
        }
    }

    private void validate(ExecutorSnapshot snapshot) {
        if (snapshot == null || blank(snapshot.executorCode()) || blank(snapshot.accountCode()) || snapshot.status() == null) {
            throw new IllegalArgumentException("Invalid executor snapshot");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
