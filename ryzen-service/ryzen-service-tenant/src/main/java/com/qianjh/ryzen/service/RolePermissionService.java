package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.RolePermission;

/**
 *
 * @author QianJH
 */
public interface RolePermissionService extends IService<RolePermission> {

    RolePermission getByUk(Long oemId, Long tenantId, Long roleId, Long permissionId);

    RolePermission create(Long oemId, Long tenantId, Long roleId, Long permissionId);

    RolePermission createIfAbsent(Long oemId, Long tenantId, Long roleId, Long permissionId);

    boolean removeById(Long oemId, Long tenantId, Long id);
}
