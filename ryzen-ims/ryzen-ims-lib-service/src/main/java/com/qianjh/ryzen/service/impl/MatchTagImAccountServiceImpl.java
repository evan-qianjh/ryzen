package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagImAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.mapper.MatchTagImAccountMapper;
import com.qianjh.ryzen.service.MatchTagImAccountService;
import com.qianjh.ryzen.service.dto.MatchTagImAccountWithMatchTagView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchTagImAccountServiceImpl extends ServiceImpl<MatchTagImAccountMapper, MatchTagImAccount> implements MatchTagImAccountService {

    @Override
    public MatchTagImAccount getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<MatchTagImAccount>()
                .eq(MatchTagImAccount::getOemId, oemId)
                .eq(MatchTagImAccount::getTenantId, tenantId)
                .eq(MatchTagImAccount::getId, id)
        );
    }

    @Override
    public MatchTagImAccount getByUk(Long oemId, Long tenantId, Long matchTagId, Long imAccountId) {
        return getOne(new LambdaQueryWrapper<MatchTagImAccount>()
                .eq(MatchTagImAccount::getOemId, oemId)
                .eq(MatchTagImAccount::getTenantId, tenantId)
                .eq(MatchTagImAccount::getMatchTagId, matchTagId)
                .eq(MatchTagImAccount::getImAccountId, imAccountId)
        );
    }

    @Override
    public List<MatchTagImAccountWithMatchTagView> getByImAccountWithMatchTag(Long oemId, Long tenantId, Long imAccountId) {
        return getBaseMapper().selectJoinList(MatchTagImAccountWithMatchTagView.class, new MPJLambdaWrapper<MatchTagImAccount>()
                .selectAll(MatchTagImAccount.class)
                .selectAs(MatchTag::getTag, MatchTagImAccountWithMatchTagView::getTag)
                //
                .innerJoin(MatchTag.class, MatchTag::getId, MatchTagImAccount::getMatchTagId)
                //
                .eq(MatchTagImAccount::getOemId, oemId)
                .eq(MatchTagImAccount::getTenantId, tenantId)
                .eq(MatchTagImAccount::getImAccountId, imAccountId)
        );
    }

    @Override
    public List<MatchTagImAccount> listByImAccount(Long oemId, Long tenantId, Long imAccountId) {
        return list(new LambdaQueryWrapper<MatchTagImAccount>()
                .eq(MatchTagImAccount::getOemId, oemId)
                .eq(MatchTagImAccount::getTenantId, tenantId)
                .eq(MatchTagImAccount::getImAccountId, imAccountId)
        );
    }

    @Override
    public List<MatchTagImAccount> listByImAccounts(Long oemId, Long tenantId, List<Long> imAccountIds) {
        if (imAccountIds.isEmpty()) {
            return Collections.emptyList();
        }
        return list(new LambdaQueryWrapper<MatchTagImAccount>()
                .eq(MatchTagImAccount::getOemId, oemId)
                .eq(MatchTagImAccount::getTenantId, tenantId)
                .in(MatchTagImAccount::getImAccountId, imAccountIds)
        );
    }

    @Override
    public Map<Long, List<MatchTagImAccount>> groupByImAccounts(Long oemId, Long tenantId, List<Long> imAccountIds) {
        return listByImAccounts(oemId, tenantId, imAccountIds)
                .stream().collect(Collectors.groupingBy(MatchTagImAccount::getImAccountId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchTagImAccount create(MatchTag matchTag, ImAccount imAccount, Operator operator) {
        MatchTagImAccount entity = MatchTagImAccount.builder()
                .oemId(matchTag.getOemId())
                .tenantId(matchTag.getTenantId())
                .matchTagId(matchTag.getId())
                .imAccountId(imAccount.getId())
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MatchTagImAccount createIfAbsent(MatchTag matchTag, ImAccount imAccount, Operator operator) {
        MatchTagImAccount entity = getByUk(matchTag.getOemId(), matchTag.getTenantId(), matchTag.getId(), imAccount.getId());
        if (entity != null) {
            return entity;
        }
        return create(matchTag, imAccount, operator);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(MatchTagImAccount entity, Operator operator) {
        return removeById(entity.getId());
    }
}
