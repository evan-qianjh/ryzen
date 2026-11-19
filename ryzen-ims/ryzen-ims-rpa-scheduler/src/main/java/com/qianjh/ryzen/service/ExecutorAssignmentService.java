package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.enums.ExecutorAssignmentStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.framework.common.dto.Operator;

/**
 *
 * @author QianJH
 */
public interface ExecutorAssignmentService extends IService<ExecutorAssignment> {

    ExecutorAssignment create(Executor executor, ImType imType, String accountCode);

    boolean updateStatus(ExecutorAssignment entity, ExecutorAssignmentStatus before, ExecutorAssignmentStatus after, Operator operator);
}
