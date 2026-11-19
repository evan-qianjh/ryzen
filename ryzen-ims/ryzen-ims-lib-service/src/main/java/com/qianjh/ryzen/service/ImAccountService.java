package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.enums.ImAccountStatus;
import com.qianjh.ryzen.enums.ImType;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.service.dto.TenantPostImAccountReq;

public interface ImAccountService extends IService<ImAccount> {

    ImAccount getById(Long oemId, Long tenantId, Long id);

    ImAccount getByUk(Long oemId, Long tenantId, ImType imType, String accountCode);

    ImAccount create(Long oemId, Long tenantId, TenantPostImAccountReq body, Operator operator);

    boolean updateStatus(ImAccount entity, ImAccountStatus before, ImAccountStatus after, Operator operator);
}
