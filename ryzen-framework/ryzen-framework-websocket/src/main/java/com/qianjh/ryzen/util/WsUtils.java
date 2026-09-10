package com.qianjh.ryzen.util;

import com.qianjh.ryzen.api.ClientInfo;
import com.qianjh.ryzen.netty.ClientManager;
import com.qianjh.ryzen.netty.handler.HeaderHandler;
import io.netty.channel.ChannelId;

/**
 *
 * @author QianJH 
 */
public class WsUtils {

    private static final String DEVICE_NAME = "websocket";
    private static final String GATEWAY = "stream";

    /**
     * 获取客户端信息
     *
     * @param clientId 客户端ID
     * @return 客户端信息
     */
    public static ClientInfo getClientInfo(ChannelId clientId) {

        String ip = ClientManager.getAttrVal(clientId, HeaderHandler.CLIENT_IP);

        return ClientInfo.builder()
                .clientIp(ip)
                .clientDevice(DEVICE_NAME)
                .clientCode(clientId.asShortText())
//                .gateway(GATEWAY)
//                .lang()
                .build();
    }
}
