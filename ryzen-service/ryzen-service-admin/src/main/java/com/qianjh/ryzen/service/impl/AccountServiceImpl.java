package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.controller.admin.dto.PatchAccountReq;
import com.qianjh.ryzen.controller.admin.dto.PostAccountReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.mapper.AccountMapper;
import com.qianjh.ryzen.service.AccountSecretService;
import com.qianjh.ryzen.service.AccountService;
import com.qianjh.ryzen.api.ClientInfo;
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
    public Account getById(Long id, Long tenantId) {
        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getId, id)
                .eq(Account::getTenantId, tenantId)
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
    public Account getByUsername(String username, Long tenantId) {
        return getOne(new LambdaQueryWrapper<Account>()
                .eq(Account::getUsername, username)
                .eq(Account::getTenantId, tenantId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Account passwordLogin(Long tenantId, ClientInfo clientInfo, String username, String password, Integer totp) {
        Account account = getByUsername(username, tenantId);
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
    public boolean patch(Long id, PatchAccountReq body, Long tenantId) {
        return lambdaUpdate()
                .set(StringUtils.isNotBlank(body.getNickname()), Account::getNickname, body.getNickname())
                .set(StringUtils.isNotBlank(body.getUsername()), Account::getUsername, body.getUsername())
                .set(body.getEnabled() != null, Account::getEnabled, body.getEnabled())
                //
                .eq(Account::getId, id)
                .eq(Account::getTenantId, tenantId)
                .update();
    }
}
