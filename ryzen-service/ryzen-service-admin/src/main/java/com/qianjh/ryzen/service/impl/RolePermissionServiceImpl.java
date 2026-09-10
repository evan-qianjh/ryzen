package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.RolePermission;
import com.qianjh.ryzen.mapper.RolePermissionMapper;
import com.qianjh.ryzen.service.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
    @Override
    public RolePermission getByUk(Long roleId, Long permissionId, Long tenantId) {
        return getOne(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getRoleId, roleId)
                .eq(RolePermission::getPermissionId, permissionId)
                .eq(RolePermission::getTenantId, tenantId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RolePermission create(Long roleId, Long permissionId, Long tenantId) {
        RolePermission entity = RolePermission.builder()
                .tenantId(tenantId)
                .roleId(roleId)
                .permissionId(permissionId)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RolePermission createIfAbsent(Long roleId, Long permissionId, Long tenantId) {
        RolePermission entity = getByUk(roleId, permissionId, tenantId);
        if (entity != null) {
            return entity;
        }
        return create(roleId, permissionId, tenantId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Long id, Long tenantId) {
        return remove(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getId, id)
                .eq(RolePermission::getTenantId, tenantId)
        );
    }
}
