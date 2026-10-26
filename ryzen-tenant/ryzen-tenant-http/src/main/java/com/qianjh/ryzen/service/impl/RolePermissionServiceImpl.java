package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.RolePermission;
import com.qianjh.ryzen.mapper.RolePermissionMapper;
import com.qianjh.ryzen.service.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void putPermissions(Long oemId, Long tenantId, Long roleId, Set<Long> targetPermissionIds) {
        // 查出已存在权限
        List<RolePermission> entities = list(new LambdaQueryWrapper<RolePermission>()
                .eq(RolePermission::getOemId, oemId)
                .eq(RolePermission::getTenantId, tenantId)
                .eq(RolePermission::getRoleId, roleId)
        );
        Map<Long, RolePermission> existPermissionIdMap = entities.stream().collect(Collectors.toMap(RolePermission::getPermissionId, Function.identity()));

        // 新增权限
        for (Long targetPermissionId : targetPermissionIds) {
            if (!existPermissionIdMap.containsKey(targetPermissionId)) {
                RolePermission entity = create(oemId, tenantId, roleId, targetPermissionId);
                log.debug("角色新增权限 ::: id={}, roleId={}, permissionId={}", entity.getId(), roleId, targetPermissionId);
            }
        }

        // 删除权限
        for (Map.Entry<Long, RolePermission> entry : existPermissionIdMap.entrySet()) {
            Long existPermissionId = entry.getKey();
            RolePermission existPermission = entry.getValue();
            if (!targetPermissionIds.contains(existPermissionId)) {
                log.debug("角色移除权限 ::: id={}, roleId={}, permissionId={}", existPermission.getId(), roleId, existPermissionId);
                removeById(existPermission.getOemId(), existPermission.getTenantId(), existPermission.getId());
            }
        }
    }
}
