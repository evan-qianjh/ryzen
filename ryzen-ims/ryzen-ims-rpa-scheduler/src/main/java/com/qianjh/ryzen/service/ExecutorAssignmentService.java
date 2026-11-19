package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.entity.ExecutorAssignment;
import com.qianjh.ryzen.enums.ImType;

/**
 *
 * @author QianJH
 */
public interface ExecutorAssignmentService extends IService<ExecutorAssignment> {

    ExecutorAssignment create(Executor executor, ImType imType, String accountCode);
}
