package com.qianjh.ryzen.framework.websocket.netty.handler;

import com.qianjh.ryzen.framework.websocket.netty.ClientManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author QianJH
 */
@Slf4j
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger WEBSOCKET_CONNECT_LOGGER = LoggerFactory.getLogger("WEBSOCKET_CONNECT_LOGGER");
    private static final Logger WEBSOCKET_ERROR_LOGGER = LoggerFactory.getLogger("WEBSOCKET_ERROR_LOGGER");

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        // WebSocket 握手成功
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WEBSOCKET_CONNECT_LOGGER.debug("[+][{}]->[{}], tenant={}, ip={}",
                    ctx.channel().id().asShortText(),
                    ClientManager.getOnline(),
                    MessageHandler.getTenantId(ctx),
                    MessageHandler.getClientIp(ctx)
            );
            return;
        }

        // 空闲超时，关闭连接
        if (evt instanceof IdleStateEvent event) {
            IdleState state = event.state();

            if (state == IdleState.ALL_IDLE || state == IdleState.READER_IDLE) {
                log.debug("clientId {} ::: {}", state, ctx.channel().id().asShortText());
                ClientManager.close(ctx.channel());
            }
            return;
        }

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        WEBSOCKET_CONNECT_LOGGER.debug("[-][{}]->[{}], tenant={}, ip={}",
                ctx.channel().id().asShortText(),
                ClientManager.getOnline(),
                MessageHandler.getTenantId(ctx),
                MessageHandler.getClientIp(ctx)
        );

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        WEBSOCKET_ERROR_LOGGER.error("[error] [{}], tenant={}, ip={}",
                ctx.channel().id().asShortText(),
                MessageHandler.getTenantId(ctx),
                MessageHandler.getClientIp(ctx),
                cause
        );

        ClientManager.close(ctx.channel());
    }
}