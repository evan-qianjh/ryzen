package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ExecutorOperationType;
import com.qianjh.ryzen.enums.ExecutorStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.mapper.ExecutorMapper;
import com.qianjh.ryzen.model.ExecutorSnapshot;
import com.qianjh.ryzen.service.ExecutorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ExecutorServiceImpl extends ServiceImpl<ExecutorMapper, Executor> implements ExecutorService {
    @Override
    public Executor getByCode(ImType type, String code) {
        return getOne(new LambdaQueryWrapper<Executor>()
                .eq(Executor::getImType, type)
                .eq(Executor::getCode, code)
        );
    }

    @Override
    public Executor getByAccount(ImType type, String accountCode) {
        return getOne(new LambdaQueryWrapper<Executor>()
                .eq(Executor::getImType, type)
                .eq(Executor::getAccountCode, accountCode)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Executor create(ImType type, String code) {
        Executor entity = Executor.builder()
                .imType(type)
                .code(code)
                .accountCode(null)
                .status(ExecutorStatus.OFFLINE)
                .currentOperation(ExecutorOperationType.NONE)
                .currentTaskId(null)
                .registeredTime(LocalDateTime.now())
                .lastHeartbeatTime(null)
                .errorMessage(null)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Executor createIfAbsent(ImType type, String code) {
        Executor entity = getByCode(type, code);
        if (entity != null) {
            return entity;
        }
        return create(type, code);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean register(Executor executor, ExecutorSnapshot snapshot) {
        LocalDateTime time = DateTimeUtils.parseMillis(snapshot.time());
        return lambdaUpdate()
                .set(Executor::getAccountCode, snapshot.accountCode())
                //
                .set(Executor::getStatus, snapshot.status())
                .set(Executor::getCurrentOperation, snapshot.currentOperation())
                .set(Executor::getCurrentTaskId, snapshot.currentTaskId())
                .set(Executor::getErrorMessage, snapshot.errorMessage())
                .set(Executor::getRegisteredTime, time)
                .set(Executor::getLastHeartbeatTime, time)
                //
                .eq(Executor::getId, executor.getId())
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean heartbeat(Executor executor, ExecutorSnapshot snapshot) {
        return lambdaUpdate()
                .set(Executor::getStatus, snapshot.status())
                .set(Executor::getCurrentOperation, snapshot.currentOperation())
                .set(Executor::getCurrentTaskId, snapshot.currentTaskId())
                .set(Executor::getErrorMessage, snapshot.errorMessage())
                .set(Executor::getLastHeartbeatTime, DateTimeUtils.parseMillis(snapshot.time()))
                //
                .eq(Executor::getId, executor.getId())
                .eq(Executor::getAccountCode, snapshot.accountCode())
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean offline(Executor executor, LocalDateTime cutoff) {
        return lambdaUpdate()
                .set(Executor::getStatus, ExecutorStatus.OFFLINE)
                .set(Executor::getCurrentOperation, ExecutorOperationType.NONE)
                .setSql("current_task_id = NULL")
                // TODO accountCode ?
                //
                .eq(Executor::getId, executor.getId())
                .lt(Executor::getLastHeartbeatTime, cutoff)
                .ne(Executor::getStatus, ExecutorStatus.OFFLINE)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean markExecutorReadyAfterTask(Long executorId, Long executorTaskId) {
        return lambdaUpdate()
                .set(Executor::getStatus, ExecutorStatus.READY)
                .set(Executor::getCurrentOperation, ExecutorOperationType.NONE)
                .setSql("current_task_id = NULL")
                .setSql("error_message = NULL")
                //
                .eq(Executor::getId, executorId)
                .eq(Executor::getCurrentTaskId, executorTaskId)
                .update();
    }
}
