package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.controller.admin.dto.PatchAccountReq;
import com.qianjh.ryzen.controller.admin.dto.PostAccountReq;
import com.qianjh.ryzen.entity.Account;

/**
 *
 * @author QianJH
 */
public interface AccountService extends IService<Account> {

    Account getById(Long oemId, Long tenantId, Long id);

    Account create(Long oemId, Long tenantId, PostAccountReq body);

    /**
     * 根据用户名查询
     *
     * @param oemId    OEM ID
     * @param username 用户名
     * @return 账户
     */
    Account getByUsername(Long oemId, String username);

    /**
     * @param oemId      OEM ID
     * @param clientInfo 客户端信息
     * @param username   用户名
     * @param password   密码
     * @param totp       TOTP
     * @return token
     */
    Account passwordLogin(Long oemId, ClientInfo clientInfo, String username, String password, Integer totp);

    boolean patch(Long oemId, Long tenantId, Long id, PatchAccountReq body);
}
