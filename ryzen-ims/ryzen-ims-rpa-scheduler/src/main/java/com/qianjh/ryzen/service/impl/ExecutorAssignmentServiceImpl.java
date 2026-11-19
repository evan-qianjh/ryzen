package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.mapper.ExecutorAssignmentMapper;
import com.qianjh.ryzen.service.ExecutorAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 *
 * @author QianJH
 */
@Service
public class ExecutorAssignmentServiceImpl extends ServiceImpl<ExecutorAssignmentMapper, ExecutorAssignment> implements ExecutorAssignmentService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ExecutorAssignment create(Executor executor, ImType imType, String accountCode) {
        ExecutorAssignment entity = ExecutorAssignment.builder()
                .executorId(executor.getId())
                .imType(imType)
                .accountCode(accountCode)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }
}
