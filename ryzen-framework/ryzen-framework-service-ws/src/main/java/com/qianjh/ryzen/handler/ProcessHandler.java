package com.qianjh.ryzen.handler;

import com.qianjh.ryzen.message.RequestMessage;
import io.netty.channel.ChannelId;

/**
 *
 * @author QianJH
 */
public interface ProcessHandler {

    /**
     * 处理
     *
     * @param clientId 客户端ID
     * @param tenantId 租户ID
     * @param request  请求
     */
    void handle(ChannelId clientId, Long tenantId, RequestMessage request);
}
