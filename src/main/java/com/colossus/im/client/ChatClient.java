package com.colossus.im.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Tlsy1
 * @since 2019-09-24 16:42
 **/
public class ChatClient {
    public void start() throws InterruptedException, IOException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        Bootstrap bs = new Bootstrap();

        bs.group(bossGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 处理来自服务端的响应信息
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                try {
                                    ByteBuf bb = (ByteBuf)msg;
                                    byte[] respByte = new byte[bb.readableBytes()];
                                    bb.readBytes(respByte);
                                    String respStr = new String(respByte, StandardCharsets.UTF_8);
                                    System.err.println("client--收到响应：" + respStr);
                                } finally{
                                    // 必须释放msg数据
                                    ReferenceCountUtil.release(msg);
                                }
                            }
                        });
                    }
                });

        // 客户端开启
        ChannelFuture cf = bs.connect("127.0.0.1", 8000).addListener(future -> {
            if(future.isSuccess()){
                System.out.println("链接服务端成功");
            }else {
                System.out.println("链接服务端失败");
            }
        }).sync();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String message = br.readLine();
        while (!message.equals("q")) {
            try {
                cf.channel().writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));
                message = br.readLine();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        cf.channel().closeFuture().sync();
    }
}
