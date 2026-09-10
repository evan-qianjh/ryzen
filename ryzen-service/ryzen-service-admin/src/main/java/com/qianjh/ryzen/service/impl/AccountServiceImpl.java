package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.controller.admin.dto.PatchAccountReq;
import com.qianjh.ryzen.controller.admin.dto.PostAccountReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.mapper.AccountMapper;
import com.qianjh.ryzen.service.AccountSecretService;
import com.qianjh.ryzen.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author QianJH
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private final AccountSecretService accountSecretService;

    @Override
    public Account getById(Long oemId, Long tenantId, Long id) {
        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getOemId, oemId)
                .eq(Account::getTenantId, tenantId)
                .eq(Account::getId, id)
        );
    }

    @Override
    public Account create(PostAccountReq body, Long tenantId) {
        Account entity = Account.builder()
                .tenantId(tenantId)
                .username(body.getUsername())
                .nickname(body.getNickname())
                .enabled(body.getEnabled())
                .administrator(false)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Override
    public Account getByUsername(Long oemId, Long tenantId, String username) {
        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getOemId, oemId)
                .eq(Account::getTenantId, tenantId)
                .eq(Account::getUsername, username)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Account passwordLogin(Long oemId, Long tenantId, ClientInfo clientInfo, String username, String password, Integer totp) {
        Account account = getByUsername(oemId, tenantId, username);
        if (Objects.isNull(account)) {
            return null;
        }

        // todo 根据场景判断totp是否必填

        // 验证密码
        boolean verify = accountSecretService.validLoginPassword(account, password, totp);

        return verify ? account : null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean patch(Long oemId, Long tenantId, Long id, PatchAccountReq body) {
        return lambdaUpdate()
                .set(StringUtils.isNotBlank(body.getNickname()), Account::getNickname, body.getNickname())
                .set(StringUtils.isNotBlank(body.getUsername()), Account::getUsername, body.getUsername())
                .set(body.getEnabled() != null, Account::getEnabled, body.getEnabled())
                //
                .eq(Account::getOemId, oemId)
                .eq(Account::getTenantId, tenantId)
                .eq(Account::getId, id)
                .update();
    }
}
