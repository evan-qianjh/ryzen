package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagServiceAccount;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;

public interface MatchTagServiceAccountService extends IService<MatchTagServiceAccount> {
    MatchTagServiceAccount getById(Long oemId, Long tenantId, Long id);

    MatchTagServiceAccount getByUk(Long oemId, Long tenantId, Long matchTagId, Long serviceAccountId);

    MatchTagServiceAccount create(MatchTag matchTag, ServiceAccount serviceAccount, Operator operator);

    MatchTagServiceAccount createIfAbsent(MatchTag matchTag, ServiceAccount serviceAccount, Operator operator);

    boolean delete(MatchTagServiceAccount entity, Operator operator);
}
