package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.ims.enums.ImAccountAssignStatus;
import com.qianjh.ryzen.ims.enums.ImAccountExecutorStatus;
import com.qianjh.ryzen.ims.enums.ImType;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.mapper.ImAccountMapper;
import com.qianjh.ryzen.service.ImAccountService;
import com.qianjh.ryzen.service.dto.TenantPostImAccountReq;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ImAccountServiceImpl extends ServiceImpl<ImAccountMapper, ImAccount> implements ImAccountService {
    @Override
    public ImAccount getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<ImAccount>()
                .eq(ImAccount::getOemId, oemId)
                .eq(ImAccount::getTenantId, tenantId)
                .eq(ImAccount::getId, id)
        );
    }

    @Override
    public ImAccount getByUk(ImType imType, String accountCode) {
        return getOne(new LambdaQueryWrapper<ImAccount>()
                .eq(ImAccount::getImType, imType)
                .eq(ImAccount::getCode, accountCode)
        );
    }

    @Override
    public ImAccount getByAssignmentId(Long assignmentId) {
        return getOne(new LambdaQueryWrapper<ImAccount>()
                .eq(ImAccount::getExecutorAssignmentId, assignmentId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ImAccount create(Long oemId, Long tenantId, TenantPostImAccountReq body, Operator operator) {
        ImAccount entity = ImAccount.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .imType(body.imType())
                .code(body.accountCode())
                .remark(body.remark())
                .assignStatus(ImAccountAssignStatus.NONE)
                .executorStatus(ImAccountExecutorStatus.NONE)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateAssignStatus(ImAccount entity, ImAccountAssignStatus before, ImAccountAssignStatus after, Operator operator) {
        return lambdaUpdate()
                .set(ImAccount::getAssignStatus, after)
                //
                .eq(ImAccount::getOemId, entity.getOemId())
                .eq(ImAccount::getTenantId, entity.getTenantId())
                .eq(ImAccount::getId, entity.getId())
                //
                .eq(ImAccount::getAssignStatus, before)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateExecutorStatus(ImAccount entity, ImAccountExecutorStatus before, ImAccountExecutorStatus after, Operator operator) {
        return lambdaUpdate()
                .set(ImAccount::getExecutorStatus, after)
                //
                .eq(ImAccount::getOemId, entity.getOemId())
                .eq(ImAccount::getTenantId, entity.getTenantId())
                .eq(ImAccount::getId, entity.getId())
                //
                .eq(ImAccount::getExecutorStatus, before)
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean recordAssignment(ImAccount entity, Long executorAssignmentId) {
        return lambdaUpdate()
                .set(ImAccount::getExecutorAssignmentId, executorAssignmentId)
                //
                .eq(ImAccount::getOemId, entity.getOemId())
                .eq(ImAccount::getTenantId, entity.getTenantId())
                .eq(ImAccount::getId, entity.getId())
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearAssignment(ImAccount entity) {
        return lambdaUpdate()
                .setSql("executor_assignment_id = NULL")
                //
                .eq(ImAccount::getOemId, entity.getOemId())
                .eq(ImAccount::getTenantId, entity.getTenantId())
                .eq(ImAccount::getId, entity.getId())
                .update();
    }
}
