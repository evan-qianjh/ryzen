package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.ims.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.ims.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.service.dto.TenantPostImAccountReq;

public interface ImAccountService extends IService<ImAccount> {

    ImAccount getById(Long oemId, Long tenantId, Long id);

    ImAccount getByUk(ImType imType, String accountCode);

    ImAccount getByAssignmentId(Long assignmentId);

    ImAccount create(Long oemId, Long tenantId, TenantPostImAccountReq body, Operator operator);

    boolean updateAssignStatus(ImAccount entity, ImAccountAssignStatus before, ImAccountAssignStatus after, Operator operator);

    boolean updateExecutorStatus(ImAccount entity, ImAccountExecutorStatus before, ImAccountExecutorStatus after, Operator operator);

    boolean recordAssignment(ImAccount entity, Long executorAssignmentId);

    boolean clearAssignment(ImAccount entity);
}
