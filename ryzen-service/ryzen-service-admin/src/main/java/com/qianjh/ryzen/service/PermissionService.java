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
    Permission getById(Long id, Long tenantId);

    Permission getByUk(String symbol, Long tenantId);

    Permission create(PostPermissionReq body, Long tenantId);

    boolean patch(Long id, PatchPermissionReq body, Long tenantId);
}
