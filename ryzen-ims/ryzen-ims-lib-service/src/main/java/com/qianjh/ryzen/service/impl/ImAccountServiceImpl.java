package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.enums.ImAccountStatus;
import com.qianjh.ryzen.enums.ImType;
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
    public ImAccount getByUk(Long oemId, Long tenantId, ImType imType, String accountCode) {
        return getOne(new LambdaQueryWrapper<ImAccount>()
                .eq(ImAccount::getOemId, oemId)
                .eq(ImAccount::getTenantId, tenantId)
                .eq(ImAccount::getImType, imType)
                .eq(ImAccount::getCode, accountCode)
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
                .status(ImAccountStatus.STOPPED)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateStatus(ImAccount entity, ImAccountStatus before, ImAccountStatus after, Operator operator) {
        return lambdaUpdate()
                .set(ImAccount::getStatus, after)
                //
                .eq(ImAccount::getOemId, entity.getOemId())
                .eq(ImAccount::getTenantId, entity.getTenantId())
                .eq(ImAccount::getId, entity.getId())
                //
                .eq(ImAccount::getStatus, before)
                .update();
    }
}
