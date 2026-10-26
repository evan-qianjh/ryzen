package com.qianjh.ryzen.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.qianjh.ryzen.controller.tenant.app.dto.PatchAccountReq;
import com.qianjh.ryzen.controller.tenant.app.dto.PostAccountReq;
import com.qianjh.ryzen.entity.Account;
import com.qianjh.ryzen.framework.common.dto.ClientInfo;

/**
 *
 * @author QianJH
 */
public interface AccountService extends IService<Account> {

    Account getById(Long oemId, Long tenantId, Long id);

    Account create(Long oemId, Long tenantId, PostAccountReq body);

    /**
     * 根据用户名查询
     * 同一个username，在同一个oem下不同tenant下唯一，即username与tenantId不共存
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
