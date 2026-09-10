package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.controller.admin.dto.PatchPermissionReq;
import com.qianjh.ryzen.controller.admin.dto.PostPermissionReq;
import com.qianjh.ryzen.entity.Permission;
import com.qianjh.ryzen.mapper.PermissionMapper;
import com.qianjh.ryzen.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Override
    public Permission getById(Long id, Long tenantId) {
        return getOne(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getId, id)
                .eq(Permission::getTenantId, tenantId)
        );
    }

    @Override
    public Permission getByUk(String symbol, Long tenantId) {
        return getOne(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getSymbol, symbol)
                .eq(Permission::getTenantId, tenantId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Permission create(PostPermissionReq body, Long tenantId) {
        Permission entity = Permission.builder()
                .tenantId(tenantId)
                .parentId(body.getParentId())
                .title(body.getTitle())
                .symbol(body.getSymbol())
                .enabled(body.getEnabled())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean patch(Long id, PatchPermissionReq body, Long tenantId) {
        return lambdaUpdate()
                .set(body.getParentId() != null, Permission::getParentId, body.getParentId())
                .set(StringUtils.isNotBlank(body.getTitle()), Permission::getTitle, body.getTitle())
                .set(body.getEnabled() != null, Permission::getEnabled, body.getEnabled())
                //
                .eq(Permission::getId, id)
                .eq(Permission::getTenantId, tenantId)
                .update();
    }
}
