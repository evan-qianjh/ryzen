package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.AccountRole;
import com.qianjh.ryzen.mapper.AccountRoleMapper;
import com.qianjh.ryzen.service.AccountRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author QianJH
 */
@Slf4j
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void putRoles(Long oemId, Long tenantId, Long accountId, Set<Long> targetRoleIds) {
        List<AccountRole> entities = list(new LambdaQueryWrapper<AccountRole>()
                .eq(AccountRole::getOemId, oemId)
                .eq(AccountRole::getTenantId, tenantId)
                .eq(AccountRole::getAccountId, accountId)
        );
        Map<Long, AccountRole> existRoleIdMap = entities.stream().collect(Collectors.toMap(AccountRole::getRoleId, Function.identity()));

        // 新增角色
        for (Long targetRoleId : targetRoleIds) {
            if (!existRoleIdMap.containsKey(targetRoleId)) {
                AccountRole entity = create(oemId, tenantId, accountId, targetRoleId);
                log.debug("用户新增角色 ::: id={}, accountId={}, roleId={}", entity.getId(), accountId, targetRoleId);
            }
        }

        // 删除角色
        for (Map.Entry<Long, AccountRole> entry : existRoleIdMap.entrySet()) {
            Long existRoleId = entry.getKey();
            AccountRole existRole = entry.getValue();
            if (!targetRoleIds.contains(existRoleId)) {
                log.debug("用户移除角色 ::: id={}, accountId={}, roleId={}", existRole.getId(), accountId, existRoleId);
                removeById(existRole.getOemId(), existRole.getTenantId(), existRole.getId());
            }
        }
    }
}
