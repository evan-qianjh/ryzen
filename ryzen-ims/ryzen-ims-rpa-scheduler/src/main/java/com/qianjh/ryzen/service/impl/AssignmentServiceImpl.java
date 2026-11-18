package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Assignment;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.mapper.AssignmentMapper;
import com.qianjh.ryzen.service.AssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 *
 * @author QianJH
 */
@Service
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Assignment create(Executor executor, ImType imType, String accountCode) {
        Assignment entity = Assignment.builder()
                .executorId(executor.getId())
                .imType(imType)
                .accountCode(accountCode)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }
}
