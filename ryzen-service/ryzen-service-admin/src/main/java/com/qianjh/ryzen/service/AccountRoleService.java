package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.AccountRole;

/**
 *
 * @author QianJH
 */
public interface AccountRoleService extends IService<AccountRole> {

    AccountRole getByUk(Long accountId, Long roleId, Long tenantId);
    AccountRole create(Long accountId, Long roleId, Long tenantId);
    AccountRole createIfAbsent(Long accountId, Long roleId, Long tenantId);

    boolean removeById(Long id, Long tenantId);
}
