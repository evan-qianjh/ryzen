package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.controller.admin.dto.PatchAccountReq;
import com.qianjh.ryzen.controller.admin.dto.PostAccountReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.api.ClientInfo;

/**
 *
 * @author QianJH
 */
public interface AccountService extends IService<Account> {

    Account getById(Long oemId, Long tenantId, Long id);

    Account create(PostAccountReq body, Long tenantId);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @param tenantId 租户ID
     * @return 账户
     */
    Account getByUsername(String username, Long tenantId);


    /**
     * @param tenantId   租户ID
     * @param clientInfo 客户端信息
     * @param username   用户名
     * @param password   密码
     * @param totp       TOTP
     * @return token
     */
    Account passwordLogin(Long tenantId, ClientInfo clientInfo, String username, String password, Integer totp);

    boolean patch(Long id, PatchAccountReq body, Long tenantId);
}
