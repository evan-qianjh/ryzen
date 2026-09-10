package com.qianjh.ryzen.service;

import com.qianjh.ryzen.controller.admin.dto.PostAccountReq;
import com.qianjh.ryzen.entity.Account;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author QianJH
 */
public interface CreateAccountService {
    Pair<Account, String> create(Long oemId, Long tenantId, PostAccountReq body);
}
