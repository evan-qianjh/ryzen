package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.controller.admin.dto.PatchRoleReq;
import com.qianjh.ryzen.controller.admin.dto.PostRoleReq;
import com.qianjh.ryzen.entity.Role;

/**
 *
 * @author QianJH
 */
public interface RoleService extends IService<Role> {
    Role getById(Long oemId, Long tenantId, Long id);

    Role getByUk(Long oemId, Long tenantId, String title);

    Role create(Long oemId, Long tenantId, PostRoleReq body);

    boolean patch(Long oemId, Long tenantId, Long id, PatchRoleReq body);
}
