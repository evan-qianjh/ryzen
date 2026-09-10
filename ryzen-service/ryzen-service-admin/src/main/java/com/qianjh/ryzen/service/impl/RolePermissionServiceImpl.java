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
    public RolePermission getByUk(Long oemId, Long tenantId, Long roleId, Long permissionId) {
        return getOne(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getOemId, oemId)
                .eq(RolePermission::getTenantId, tenantId)
                .eq(RolePermission::getRoleId, roleId)
                .eq(RolePermission::getPermissionId, permissionId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RolePermission create(Long oemId, Long tenantId, Long roleId, Long permissionId) {
        RolePermission entity = RolePermission.builder()
                .oemId(oemId)
                .tenantId(tenantId)
                .roleId(roleId)
                .permissionId(permissionId)
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RolePermission createIfAbsent(Long oemId, Long tenantId, Long roleId, Long permissionId) {
        RolePermission entity = getByUk(oemId, tenantId, roleId, permissionId);
        if (entity != null) {
            return entity;
        }
        return create(oemId, tenantId, roleId, permissionId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Long oemId, Long tenantId, Long id) {
        return remove(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getId, id)
                .eq(RolePermission::getOemId, oemId)
                .eq(RolePermission::getTenantId, tenantId)
        );
    }
}
