package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagServiceAccount;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.mapper.MatchTagServiceAccountMapper;
import com.qianjh.ryzen.service.MatchTagServiceAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MatchTagServiceAccountServiceImpl extends ServiceImpl<MatchTagServiceAccountMapper, MatchTagServiceAccount> implements MatchTagServiceAccountService {

    @Override
    public MatchTagServiceAccount getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<MatchTagServiceAccount>()
                .eq(MatchTagServiceAccount::getOemId, oemId)
                .eq(MatchTagServiceAccount::getTenantId, tenantId)
                .eq(MatchTagServiceAccount::getId, id)
        );
    }

    @Override
    public MatchTagServiceAccount getByUk(Long oemId, Long tenantId, Long matchTagId, Long serviceAccountId) {
        return getOne(new LambdaQueryWrapper<MatchTagServiceAccount>()
                .eq(MatchTagServiceAccount::getOemId, oemId)
                .eq(MatchTagServiceAccount::getTenantId, tenantId)
                .eq(MatchTagServiceAccount::getMatchTagId, matchTagId)
                .eq(MatchTagServiceAccount::getServiceAccountId, serviceAccountId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchTagServiceAccount create(MatchTag matchTag, ServiceAccount serviceAccount, Operator operator) {
        MatchTagServiceAccount entity = MatchTagServiceAccount.builder()
                .oemId(matchTag.getOemId())
                .tenantId(matchTag.getTenantId())
                .matchTagId(matchTag.getId())
                .serviceAccountId(serviceAccount.getId())
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchTagServiceAccount createIfAbsent(MatchTag matchTag, ServiceAccount serviceAccount, Operator operator) {
        MatchTagServiceAccount entity = getByUk(matchTag.getOemId(), matchTag.getTenantId(), matchTag.getId(), serviceAccount.getId());
        if (entity != null) {
            return entity;
        }
        return create(matchTag, serviceAccount, operator);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(MatchTagServiceAccount entity, Operator operator) {
        return removeById(entity.getId());
    }
}
