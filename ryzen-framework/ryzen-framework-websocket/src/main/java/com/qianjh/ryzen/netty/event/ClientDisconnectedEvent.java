package com.qianjh.ryzen.netty.event;

import io.netty.channel.ChannelId;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author QianJH
 */
@Getter
@AllArgsConstructor
public class ClientDisconnectedEvent implements ClientEvent {

    private final ChannelId channelId;

}
