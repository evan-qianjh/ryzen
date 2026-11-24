package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.AccountBusiness;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.service.dto.TenantPatchAccountBusinessReq;
import com.qianjh.ryzen.service.dto.TenantPostAccountBusinessReq;
import com.qianjh.ryzen.tenant.enums.AccountBusinessType;

/**
 *
 * @author QianJH
 */
public interface AccountBusinessService extends IService<AccountBusiness> {
    AccountBusiness getById(Long oemId, Long tenantId, Long id);

    AccountBusiness getByUk(Long oemId, Long tenantId, Long accountId, AccountBusinessType type);

    AccountBusiness create(Long oemId, Long tenantId, TenantPostAccountBusinessReq body, Operator operator);

    boolean patch(AccountBusiness entity, TenantPatchAccountBusinessReq body, Operator operator);
}
