package com.qianjh.ryzen.framework.websocket.netty;

import com.qianjh.ryzen.framework.websocket.netty.handler.HeaderHandler;
import com.qianjh.ryzen.framework.websocket.netty.handler.IdleHandler;
import com.qianjh.ryzen.framework.websocket.netty.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Starter {

    private final int port;
    private final String path;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private Channel serverChannel;

    public Starter(int port, String path) {
        this.port = port;
        this.path = path;
    }

    public synchronized void start() throws InterruptedException {
        if (serverChannel != null && serverChannel.isActive()) {
            return;
        }

        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new HttpServerCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(8192));

                            ch.pipeline().addLast(new HeaderHandler());
                            ch.pipeline().addLast(
                                    new WebSocketServerProtocolHandler(
                                            path, null, true, 65535 * 5
                                    )
                            );
                            ch.pipeline().addLast(
                                    new IdleStateHandler(
                                            30,
                                            Long.MAX_VALUE,
                                            Long.MAX_VALUE,
                                            TimeUnit.SECONDS
                                    )
                            );
                            ch.pipeline().addLast(new IdleHandler());
                            ch.pipeline().addLast(new MessageHandler());
                        }
                    });

            // 绑定端口成功后立即返回，不等待服务关闭
            serverChannel = bootstrap.bind(port).sync().channel();

            System.out.println(
                    "WebSocket server started: "
                            + serverChannel.localAddress()
            );

        } catch (InterruptedException e) {
            shutdownGroups();
            Thread.currentThread().interrupt();
            throw e;
        } catch (RuntimeException e) {
            shutdownGroups();
            throw e;
        }
    }

    public synchronized void stop() {
        if (serverChannel != null) {
            serverChannel.close().syncUninterruptibly();
            serverChannel = null;
        }

        shutdownGroups();

        System.out.println("WebSocket server stopped");
    }

    private void shutdownGroups() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().syncUninterruptibly();
            bossGroup = null;
        }

        if (workGroup != null) {
            workGroup.shutdownGracefully().syncUninterruptibly();
            workGroup = null;
        }
    }
}