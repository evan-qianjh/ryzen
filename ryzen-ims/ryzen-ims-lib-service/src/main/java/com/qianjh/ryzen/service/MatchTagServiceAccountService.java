package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagServiceAccount;
import com.qianjh.ryzen.entity.ServiceAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;
import com.qianjh.ryzen.service.dto.MatchTagServiceAccountWithMatchTagView;

import java.util.List;
import java.util.Map;

public interface MatchTagServiceAccountService extends IService<MatchTagServiceAccount> {
    MatchTagServiceAccount getById(Long oemId, Long tenantId, Long id);

    MatchTagServiceAccount getByUk(Long oemId, Long tenantId, Long matchTagId, Long serviceAccountId);

    List<MatchTagServiceAccount> listByServiceAccounts(Long oemId, Long tenantId, List<Long> serviceAccountIds);

    Map<Long, List<MatchTagServiceAccount>> groupByServiceAccounts(Long oemId, Long tenantId, List<Long> serviceAccountIds);

    List<MatchTagServiceAccountWithMatchTagView> getByServiceAccountWithMatchTag(Long oemId, Long tenantId, Long serviceAccountId);

    List<MatchTagServiceAccountWithMatchTagView> listByServiceAccountsWithMatchTag(Long oemId, Long tenantId, List<Long> serviceAccountIds);
    Map<Long, List<MatchTagServiceAccountWithMatchTagView>> groupByServiceAccountsWithMatchTag(Long oemId, Long tenantId, List<Long> serviceAccountIds);

    MatchTagServiceAccount create(MatchTag matchTag, ServiceAccount serviceAccount, Operator operator);

    MatchTagServiceAccount createIfAbsent(MatchTag matchTag, ServiceAccount serviceAccount, Operator operator);

    boolean delete(MatchTagServiceAccount entity, Operator operator);
}
