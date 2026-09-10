package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.controller.admin.dto.PatchPermissionReq;
import com.qianjh.ryzen.controller.admin.dto.PostPermissionReq;
import com.qianjh.ryzen.entity.Permission;

/**
 *
 * @author QianJH
 */
public interface PermissionService extends IService<Permission> {
    Permission getById(Long oemId, Long tenantId, Long id);

    Permission getByUk(Long oemId, Long tenantId, String symbol);

    Permission create(Long oemId, Long tenantId, PostPermissionReq body);

    boolean patch(Long oemId, Long tenantId, Long id, PatchPermissionReq body);
}
