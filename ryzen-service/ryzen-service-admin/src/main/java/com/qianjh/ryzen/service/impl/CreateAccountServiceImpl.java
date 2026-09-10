package com.qianjh.ryzen.service.impl;

import com.qianjh.ryzen.controller.admin.dto.PostAccountReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.service.AccountSecretService;
import com.qianjh.ryzen.service.AccountService;
import com.qianjh.ryzen.service.CreateAccountService;
import com.qianjh.ryzen.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAccountServiceImpl implements CreateAccountService {

    private final AccountService accountService;
    private final AccountSecretService accountSecretService;

    @Override
    public Pair<Account, String> create(Long oemId, Long tenantId, PostAccountReq body) {
        Account account = accountService.create(oemId, tenantId, body);

        String password = PasswordUtils.generate();

        accountSecretService.create(account, password);

        return Pair.of(account, password);
    }
}
