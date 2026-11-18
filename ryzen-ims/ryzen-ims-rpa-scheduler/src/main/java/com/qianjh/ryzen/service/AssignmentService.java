package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Assignment;
import com.qianjh.ryzen.entity.Executor;
import com.qianjh.ryzen.enums.ImType;

/**
 *
 * @author QianJH
 */
public interface AssignmentService extends IService<Assignment> {

    Assignment create(Executor executor, ImType imType, String accountCode);
}
