package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.ims.enums.ExecutorOperationType;
import com.qianjh.ryzen.ims.enums.ExecutorStatus;
import com.qianjh.ryzen.framework.common.util.DateTimeUtils;
import com.qianjh.ryzen.mapper.ExecutorMapper;
import com.qianjh.ryzen.ims.model.ExecutorSnapshot;
import com.qianjh.ryzen.service.ExecutorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ExecutorServiceImpl extends ServiceImpl<ExecutorMapper, Executor> implements ExecutorService {
    @Override
    public Executor getByCode(String code) {
        return getOne(new LambdaQueryWrapper<Executor>()
                .eq(Executor::getCode, code)
        );
    }

    @Override
    public Executor getByAssignment(Long assignmentId) {
        return getOne(new LambdaQueryWrapper<Executor>()
                .eq(Executor::getCurrentAssigmentId, assignmentId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Executor create(String code) {
        Executor entity = Executor.builder()
                .code(code)
                .status(ExecutorStatus.OFFLINE)
                .currentAssigmentId(null)
                .currentOperation(ExecutorOperationType.NONE)
                .currentTaskId(null)
                .registeredTime(null)
                .lastHeartbeatTime(null)
                .errorMessage(null)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Executor createIfAbsent(String code) {
        Executor entity = getByCode(code);
        if (entity != null) {
            return entity;
        }
        return create(code);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean register(Executor executor, ExecutorSnapshot snapshot) {
        LocalDateTime time = LocalDateTime.now();
        return lambdaUpdate()
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
    public boolean heartbeat(Executor executor, ExecutorSnapshot snapshot, long timestamp) {
        return lambdaUpdate()
                .set(Executor::getStatus, snapshot.status())
                .set(Executor::getCurrentOperation, snapshot.currentOperation())
                .set(Executor::getCurrentTaskId, snapshot.currentTaskId())
                .set(Executor::getErrorMessage, snapshot.errorMessage())
                .set(Executor::getLastHeartbeatTime, DateTimeUtils.parseMillis(timestamp))
                //
                .eq(Executor::getId, executor.getId())
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
    public boolean assign(Executor executor, ExecutorAssignment assignment) {
        return lambdaUpdate()
                .set(Executor::getCurrentAssigmentId, assignment.getId())
                //
                .eq(Executor::getId, executor.getId())
                .isNull(Executor::getCurrentAssigmentId)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean release(Executor executor, ExecutorAssignment assignment) {
        return lambdaUpdate()
                .setSql("current_assigment_id = NULL")
                //
                .eq(Executor::getId, executor.getId())
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
