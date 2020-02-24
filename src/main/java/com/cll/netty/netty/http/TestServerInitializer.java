package com.cll.netty.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class TestServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 向管道加入处理器
        // 得到管道
        ChannelPipeline pipeline = ch.pipeline();
        // 加入一个netty 提供的httpServerCode codec => [coder - decoder]
        // HttpServerCodec 说明
        // 1. HttpServerCodec 是netty提供的处理http的编-解码器
        pipeline.addLast("myHttpServerCodec", new HttpServerCodec());
        // 2. 增加一个自定义的Handler
        pipeline.addLast("myTestHttpServerHandler", new TestHttpServerHandler());
    }
}
