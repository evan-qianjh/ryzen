package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.RolePermission;

/**
 *
 * @author QianJH
 */
public interface RolePermissionService extends IService<RolePermission> {

    RolePermission getByUk(Long roleId, Long permissionId, Long tenantId);

    RolePermission create(Long roleId, Long permissionId, Long tenantId);

    RolePermission createIfAbsent(Long roleId, Long permissionId, Long tenantId);

    boolean removeById(Long id, Long tenantId);
}
