package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.ims.enums.ServiceAccountStatus;
import com.qianjh.ryzen.mapper.ServiceAccountMapper;
import com.qianjh.ryzen.service.ServiceAccountService;
import com.qianjh.ryzen.service.dto.TenantPatchServiceAccountReq;
import com.qianjh.ryzen.service.dto.TenantPostServiceAccountReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ServiceAccountServiceImpl extends ServiceImpl<ServiceAccountMapper, ServiceAccount> implements ServiceAccountService {
    @Override
    public ServiceAccount getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<ServiceAccount>()
                .eq(ServiceAccount::getOemId, oemId)
                .eq(ServiceAccount::getTenantId, tenantId)
                .eq(ServiceAccount::getId, id)
        );
    }

    @Override
    public ServiceAccount getByUk(Long oemId, Long tenantId, Long accountId) {
        return getOne(new LambdaQueryWrapper<ServiceAccount>()
                .eq(ServiceAccount::getOemId, oemId)
                .eq(ServiceAccount::getTenantId, tenantId)
                .eq(ServiceAccount::getAccountId, accountId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ServiceAccount create(Long oemId, Long tenantId, TenantPostServiceAccountReq body, Operator operator) {
        Long accountId = body.accountId();

        ServiceAccount entity = ServiceAccount.builder()
                .id(accountId)
                .oemId(oemId)
                .tenantId(tenantId)
                .accountId(accountId)
                .status(ServiceAccountStatus.ENABLED)
                .online(false)
                .remark(body.remark())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean patch(ServiceAccount entity, TenantPatchServiceAccountReq body, Operator operator) {
        return lambdaUpdate()
                .set(body.status() != null, ServiceAccount::getStatus, body.status())
                .set(body.remark() != null, ServiceAccount::getRemark, body.remark())
                //
                .eq(ServiceAccount::getOemId, entity.getOemId())
                .eq(ServiceAccount::getTenantId, entity.getTenantId())
                .eq(ServiceAccount::getId, entity.getId())
                .update();
    }
}
