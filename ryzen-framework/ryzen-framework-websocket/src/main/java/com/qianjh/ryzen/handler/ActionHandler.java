package com.qianjh.ryzen.handler;

import com.qianjh.ryzen.message.RequestMessage;
import io.netty.channel.Channel;

/**
 *
 * @author QianJH
 */
public interface ActionHandler {

    /**
     * 处理
     *
     * @param client   客户端
     * @param tenantId 租户ID
     * @param request  请求
     */
    void handle(Channel client, Long tenantId, RequestMessage request);
}
