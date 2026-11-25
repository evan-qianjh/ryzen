package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.service.dto.TenantPatchServiceAccountReq;
import com.qianjh.ryzen.service.dto.TenantPostServiceAccountReq;

public interface ServiceAccountService extends IService<ServiceAccount> {
    ServiceAccount getById(Long oemId, Long tenantId, Long id);

    ServiceAccount getByUk(Long oemId, Long tenantId, Long accountId);

    ServiceAccount create(Long oemId, Long tenantId, TenantPostServiceAccountReq body, Operator operator);

    boolean patch(ServiceAccount entity, TenantPatchServiceAccountReq body, Operator operator);
}
