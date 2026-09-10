package com.qianjh.ryzen.netty.handler;

import com.qianjh.ryzen.netty.ClientManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author QianJH
 */
@Slf4j
public class IdleHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();
            //
            if (state == IdleState.ALL_IDLE || state == IdleState.READER_IDLE) {
                log.debug("clientId {} ::: {}", state, ctx.channel().id().asShortText());
                ClientManager.close(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
