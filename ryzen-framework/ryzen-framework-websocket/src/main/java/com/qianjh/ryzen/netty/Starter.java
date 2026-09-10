package com.qianjh.ryzen.netty;


import com.qianjh.ryzen.netty.handler.HeaderHandler;
import com.qianjh.ryzen.netty.handler.IdleHandler;
import com.qianjh.ryzen.netty.handler.MessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
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

/**
 * @author QianJH
 */
public class Starter {

    private final int port;
    private final String path;

    public Starter(int port, String path) {
        this.port = port;
        this.path = path;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec());
//                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new HttpObjectAggregator(8192));

                        // 处理请求头
                        ch.pipeline().addLast(new HeaderHandler());
                        // HTTP升级到WS协议，要想获取http参数，在此之前
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler(path, null, true, 65535 * 5));
                        // 访问超时
//                        ch.pipeline().addLast(new LeaveHandler(30));
                        ch.pipeline().addLast(new IdleStateHandler(30, Long.MAX_VALUE, Long.MAX_VALUE, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new IdleHandler());
                        // 业务处理
                        ch.pipeline().addLast(new MessageHandler());
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
