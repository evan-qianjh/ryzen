package com.qianjh.ryzen.service;


import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.entity.AccountSecret;

/**
 *
 * @author QianJH
 */
public interface AccountSecretService extends IService<AccountSecret> {

    AccountSecret getByAccount(Account account);

    AccountSecret create(Account account, String loginPassword);

    /**
     * 修改(用于修改密码)
     *
     * @param account          账户
     * @param newLoginPassword 新登录密码
     * @param oldLoginPassword 老登录密码
     * @return 结果
     */
    boolean modifyLoginPassword(Account account, String newLoginPassword, String oldLoginPassword);

    boolean putLoginPassword(Account account, String loginPassword);


    /**
     * 校验登录密码
     *
     * @param account       账号ID
     * @param loginPassword 登录密码
     * @param totp          基于时间的一次性密码
     * @return 是否匹配
     */
    boolean validLoginPassword(Account account, String loginPassword, Integer totp);

    /**
     * 设置TOTP秘钥
     *
     * @param account 账户
     * @param issuer  应用的名称或公司名
     * @return 二维码地址
     */
    String initTotpSecret(Account account, String issuer);
}
