package com.colossus.im.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Tlsy1
 * @since 2019-09-24 16:35
 **/
public class ChatServer {
    public void start(){
        // 创建mainReactor
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        // 创建工作线程组
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                // 组装NioEventLoopGroup
                .group(boosGroup, workerGroup)
                // 设置channel类型为NIO类型
                .channel(NioServerSocketChannel.class)
                // 设置连接配置参数
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 配置入站、出站事件handler
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        // 配置入站、出站事件channel
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf bb = (ByteBuf)msg;
                                // 创建一个和buf同等长度的字节数组
                                byte[] reqByte = new byte[bb.readableBytes()];
                                // 将buf中的数据读取到数组中
                                bb.readBytes(reqByte);
                                String reqStr = new String(reqByte, StandardCharsets.UTF_8);
                                System.out.println(reqStr);
                                ctx.writeAndFlush(Unpooled.wrappedBuffer("服务端已收到".getBytes()));

                            }
                        });
                    }
                });

        // 绑定端口
        int port = 8000;
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
