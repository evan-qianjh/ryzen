package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.AccountRole;
import com.qianjh.ryzen.mapper.AccountRoleMapper;
import com.qianjh.ryzen.service.AccountRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author QianJH
 */
@Service
public class AccountRoleServiceImpl extends ServiceImpl<AccountRoleMapper, AccountRole> implements AccountRoleService {
    @Override
    public AccountRole getByUk(Long oemId, Long tenantId, Long accountId, Long roleId) {
        return getOne(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getOemId, oemId)
                .eq(AccountRole::getTenantId, tenantId)
                .eq(AccountRole::getAccountId, accountId)
                .eq(AccountRole::getRoleId, roleId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AccountRole create(Long oemId, Long tenantId, Long accountId, Long roleId) {
        AccountRole entity = AccountRole.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .accountId(accountId)
                .roleId(roleId)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AccountRole createIfAbsent(Long oemId, Long tenantId, Long accountId, Long roleId) {
        AccountRole entity = getByUk(oemId, tenantId, accountId, roleId);
        if (entity != null) {
            return entity;
        }
        return create(oemId, tenantId, accountId, roleId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Long oemId, Long tenantId, Long id) {
        return remove(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getOemId, oemId)
                .eq(AccountRole::getTenantId, tenantId)
                .eq(AccountRole::getId, id)
        );
    }
}
