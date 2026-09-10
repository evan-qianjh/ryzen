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
    public Permission getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getOemId, oemId)
                .eq(Permission::getTenantId, tenantId)
                .eq(Permission::getId, id)
        );
    }

    @Override
    public Permission getByUk(Long oemId, Long tenantId, String symbol) {
        return getOne(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getOemId, oemId)
                .eq(Permission::getTenantId, tenantId)
                .eq(Permission::getSymbol, symbol)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Permission create(Long oemId, Long tenantId, PostPermissionReq body) {
        Permission entity = Permission.builder()
                .oemId(oemId)
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
    public boolean patch(Long oemId, Long tenantId, Long id, PatchPermissionReq body) {
        return lambdaUpdate()
                .set(body.getParentId() != null, Permission::getParentId, body.getParentId())
                .set(StringUtils.isNotBlank(body.getTitle()), Permission::getTitle, body.getTitle())
                .set(body.getEnabled() != null, Permission::getEnabled, body.getEnabled())
                //
                .eq(Permission::getOemId, oemId)
                .eq(Permission::getTenantId, tenantId)
                .eq(Permission::getId, id)
                .update();
    }
}
