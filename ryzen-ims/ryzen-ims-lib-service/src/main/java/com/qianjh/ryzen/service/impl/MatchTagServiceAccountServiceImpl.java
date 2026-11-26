package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagServiceAccount;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.mapper.MatchTagServiceAccountMapper;
import com.qianjh.ryzen.service.MatchTagServiceAccountService;
import com.qianjh.ryzen.service.dto.MatchTagServiceAccountWithMatchTagView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    public List<MatchTagServiceAccount> listByServiceAccounts(Long oemId, Long tenantId, List<Long> serviceAccountIds) {
        if (serviceAccountIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<MatchTagServiceAccount>()
                .eq(MatchTagServiceAccount::getOemId, oemId)
                .eq(MatchTagServiceAccount::getTenantId, tenantId)
                .in(MatchTagServiceAccount::getServiceAccountId, serviceAccountIds)
        );
    }

    @Override
    public Map<Long, List<MatchTagServiceAccount>> groupByServiceAccounts(Long oemId, Long tenantId, List<Long> serviceAccountIds) {
        return listByServiceAccounts(oemId, tenantId, serviceAccountIds)
                .stream().collect(Collectors.groupingBy(MatchTagServiceAccount::getServiceAccountId));
    }

    @Override
    public List<MatchTagServiceAccountWithMatchTagView> getByServiceAccountWithMatchTag(Long oemId, Long tenantId, Long serviceAccountId) {
        return getBaseMapper().selectJoinList(MatchTagServiceAccountWithMatchTagView.class, new MPJLambdaWrapper<MatchTagServiceAccount>()
                .selectAll(MatchTagServiceAccount.class)
                .selectAs(MatchTag::getTag, MatchTagServiceAccountWithMatchTagView::getTag)
                //
                .innerJoin(MatchTag.class, MatchTag::getId, MatchTagServiceAccount::getMatchTagId)
                //
                .eq(MatchTagServiceAccount::getOemId, oemId)
                .eq(MatchTagServiceAccount::getTenantId, tenantId)
                .eq(MatchTagServiceAccount::getServiceAccountId, serviceAccountId)
        );
    }

    @Override
    public List<MatchTagServiceAccountWithMatchTagView> listByServiceAccountsWithMatchTag(Long oemId, Long tenantId, List<Long> serviceAccountIds) {
        if (serviceAccountIds.isEmpty()) {
            return Collections.emptyList();
        }

        return getBaseMapper().selectJoinList(MatchTagServiceAccountWithMatchTagView.class, new MPJLambdaWrapper<MatchTagServiceAccount>()
                .selectAll(MatchTagServiceAccount.class)
                .selectAs(MatchTag::getTag, MatchTagServiceAccountWithMatchTagView::getTag)
                //
                .innerJoin(MatchTag.class, MatchTag::getId, MatchTagServiceAccount::getMatchTagId)
                //
                .eq(MatchTagServiceAccount::getOemId, oemId)
                .eq(MatchTagServiceAccount::getTenantId, tenantId)
                .in(MatchTagServiceAccount::getServiceAccountId, serviceAccountIds)
        );
    }

    @Override
    public Map<Long, List<MatchTagServiceAccountWithMatchTagView>> groupByServiceAccountsWithMatchTag(Long oemId, Long tenantId, List<Long> serviceAccountIds) {
        return listByServiceAccountsWithMatchTag(oemId, tenantId, serviceAccountIds)
                .stream().collect(Collectors.groupingBy(MatchTagServiceAccountWithMatchTagView::getServiceAccountId));
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
