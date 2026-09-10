package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.controller.admin.dto.PatchRoleReq;
import com.qianjh.ryzen.controller.admin.dto.PostRoleReq;
import com.qianjh.ryzen.entity.Role;
import com.qianjh.ryzen.mapper.RoleMapper;
import com.qianjh.ryzen.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Override
    public Role getById(Long id, Long tenantId) {
        return getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getId, id)
                .eq(Role::getTenantId, tenantId)
        );
    }

    @Override
    public Role getByUk(String title, Long tenantId) {
        return getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getTitle, title)
                .eq(Role::getTenantId, tenantId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role create(PostRoleReq body, Long tenantId) {
        Role entity = Role.builder()
                .tenantId(tenantId)
                .title(body.getTitle())
                .enabled(body.getEnabled())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean patch(Long id, PatchRoleReq body, Long tenantId) {
        return lambdaUpdate()
                .set(body.getEnabled() != null, Role::getEnabled, body.getEnabled())
                //
                .eq(Role::getId, id)
                .eq(Role::getTenantId, tenantId)
                .update();
    }
}
