package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.AccountRole;

/**
 *
 * @author QianJH
 */
public interface AccountRoleService extends IService<AccountRole> {

    AccountRole getByUk(Long oemId, Long tenantId, Long accountId, Long roleId);

    AccountRole create(Long oemId, Long tenantId, Long accountId, Long roleId);

    AccountRole createIfAbsent(Long oemId, Long tenantId, Long accountId, Long roleId);

    boolean removeById(Long oemId, Long tenantId, Long id);
}
