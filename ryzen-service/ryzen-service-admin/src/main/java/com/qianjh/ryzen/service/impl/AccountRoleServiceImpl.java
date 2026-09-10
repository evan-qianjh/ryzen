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
    public AccountRole getByUk(Long accountId, Long roleId, Long tenantId) {
        return getOne(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getAccountId, accountId)
                .eq(AccountRole::getRoleId, roleId)
                .eq(AccountRole::getTenantId, tenantId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AccountRole create(Long accountId, Long roleId, Long tenantId) {
        AccountRole entity = AccountRole.builder()
                .tenantId(tenantId)
                .accountId(accountId)
                .roleId(roleId)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AccountRole createIfAbsent(Long accountId, Long roleId, Long tenantId) {
        AccountRole entity = getByUk(accountId, roleId, tenantId);
        if (entity != null) {
            return entity;
        }
        return create(accountId, roleId, tenantId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Long id, Long tenantId) {
        return remove(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getId, id)
                .eq(AccountRole::getTenantId, tenantId)
        );
    }
}
