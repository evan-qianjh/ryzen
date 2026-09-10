package com.qianjh.ryzen.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

/**
 *
 * @author QianJH
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param clientId 客户端ID
     * @param tenantId 租户ID
     * @param token    token
     */
    void login(ChannelId clientId, Long tenantId, String token);

    /**
     * 登出
     *
     * @param client 客户端
     */
    void logout(Channel client);
}
