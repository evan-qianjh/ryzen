package com.qianjh.ryzen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountSecret;
import com.qianjh.ryzen.mapper.AccountSecretMapper;
import com.qianjh.ryzen.service.AccountSecretService;
import com.qianjh.ryzen.service.RyzenStorageCryptoService;
import com.qianjh.ryzen.util.PasswordUtils;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author QianJH
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSecretServiceImpl extends ServiceImpl<AccountSecretMapper, AccountSecret> implements AccountSecretService {

    private final RyzenStorageCryptoService ryzenStorageCryptoService;

    @Override
    public AccountSecret getByAccount(Account account) {
        return getOne(new LambdaQueryWrapper<AccountSecret>()
                .eq(AccountSecret::getAccountId, account.getId())
                .eq(AccountSecret::getTenantId, account.getTenantId())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AccountSecret create(Account account, String loginPassword) {
        //
        String hashedLoginPassword = PasswordUtils.hash(loginPassword);

        AccountSecret entity = AccountSecret.builder()
                .id(account.getId())
                .tenantId(account.getTenantId())
                .accountId(account.getId())
                .loginPassword(hashedLoginPassword)
                .createdTime(LocalDateTime.now())
                .build();
        save(entity);
        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyLoginPassword(Account account, String newLoginPassword, String oldLoginPassword) {
        AccountSecret userSecret = getByAccount(account);

        // 校验老密码
        Assert.isTrue(PasswordUtils.check(oldLoginPassword, userSecret.getLoginPassword()), "密码错误");

        // 新密码
        String hashedNewLoginPassword = PasswordUtils.hash(newLoginPassword);

        // 修改
        return lambdaUpdate()
                .set(AccountSecret::getLoginPassword, hashedNewLoginPassword)
                //
                .eq(AccountSecret::getId, userSecret.getId())
                .eq(AccountSecret::getTenantId, userSecret.getTenantId())
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean putLoginPassword(Account account, String loginPassword) {
        AccountSecret userSecret = getByAccount(account);

        // 新密码
        String hashedNewLoginPassword = PasswordUtils.hash(loginPassword);

        // 修改
        return lambdaUpdate()
                .set(AccountSecret::getLoginPassword, hashedNewLoginPassword)
                //
                .eq(AccountSecret::getId, userSecret.getId())
                .eq(AccountSecret::getTenantId, userSecret.getTenantId())
                .update();
    }

    @Override
    public boolean validLoginPassword(Account account, String loginPassword, Integer totp) {
        //
        AccountSecret accountSecret = getByAccount(account);
        if (Objects.isNull(accountSecret)) {
            log.error("AccountSecret is null ::: accountId={}", account.getId());
            return false;
        }

        // 验证TOPT
        if (StringUtils.isNotBlank(accountSecret.getTotpSecret())) {
            String toptSecret = ryzenStorageCryptoService.decrypt(accountSecret.getTotpSecret());
            GoogleAuthenticator authenticator = new GoogleAuthenticator();
            boolean authorize = authenticator.authorize(toptSecret, totp);
            if (!authorize) {
                return false;
            }
        }

        // 验证密码
        return PasswordUtils.check(loginPassword, accountSecret.getLoginPassword());
    }


    @Override
    public String initTotpSecret(Account account, String issuer) {
        AccountSecret accountSecret = getByAccount(account);
        Assert.notNull(accountSecret, "AccountSecret is null");

        GoogleAuthenticator authenticator = new GoogleAuthenticator();
        GoogleAuthenticatorKey credentials = authenticator.createCredentials();
        String totpSecret = credentials.getKey();

        boolean update = lambdaUpdate()
                .set(AccountSecret::getTotpSecret, totpSecret)
                .eq(AccountSecret::getId, accountSecret.getId())
                .isNull(AccountSecret::getTotpSecret)
                .update();
        if (!update) {
            return null;
        }
        return buildTotpSecretQrCodeUrl(issuer, account.getUsername(), totpSecret);
    }


    /**
     * 构建QRCode
     *
     * @param issuer  应用的名称或公司名
     * @param account 用户的账号名
     * @param secret  密钥
     * @return QRCode
     */
    private String buildTotpSecretQrCodeUrl(String issuer, String account, String secret) {
        String format = "otpauth://totp/%s:%s?secret=%s&issuer=%s";
        return String.format(format, issuer, account, secret, issuer);
    }
}
