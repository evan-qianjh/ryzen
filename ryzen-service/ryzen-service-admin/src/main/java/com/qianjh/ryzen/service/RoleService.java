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
    Role getById(Long id, Long tenantId);

    Role getByUk(String title, Long tenantId);

    Role create(PostRoleReq body, Long tenantId);

    boolean patch(Long id, PatchRoleReq body, Long tenantId);
}
