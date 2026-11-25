package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.ImAccount;
import com.qianjh.ryzen.entity.MatchTag;
import com.qianjh.ryzen.entity.MatchTagImAccount;
import com.qianjh.ryzen.framework.common.dto.Operator;

public interface MatchTagImAccountService extends IService<MatchTagImAccount> {
    MatchTagImAccount getById(Long oemId, Long tenantId, Long id);

    MatchTagImAccount getByUk(Long oemId, Long tenantId, Long matchTagId, Long imAccountId);

    MatchTagImAccount create(MatchTag matchTag, ImAccount imAccount, Operator operator);

    MatchTagImAccount createIfAbsent(MatchTag matchTag, ImAccount imAccount, Operator operator);

    boolean delete(MatchTagImAccount entity, Operator operator);
}
