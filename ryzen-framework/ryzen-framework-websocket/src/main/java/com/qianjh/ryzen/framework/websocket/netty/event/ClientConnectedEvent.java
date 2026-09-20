package com.qianjh.ryzen.framework.websocket.netty.event;

import io.netty.channel.ChannelId;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author QianJH
 */
@Getter
@AllArgsConstructor
public class ClientConnectedEvent implements ClientEvent {
    private final ChannelId channelId;
}
