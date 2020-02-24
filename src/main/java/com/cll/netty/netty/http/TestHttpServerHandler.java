package com.cll.netty.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * 说明
 * 1. SimpleChannelInboundHandler 是ChannelInboundHandler
 * 2. HttpObject 客户端和服务器商相互通讯是数据被封装成HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断msg是不是HttpRequest请求
        if(msg instanceof HttpRequest){
            System.out.println("msg 类型 = " + msg.getClass());
            System.out.println("客户端地址"+ ctx.channel().remoteAddress());

            // http服务过滤资源
            HttpRequest httpRequest = (HttpRequest)msg;
            URI uri = new URI(httpRequest.uri());
            if("/favicon.ico".equals(uri.getPath())){
                System.out.println("---过滤，不继续");
                return;
               // return;
            }
            // 回复信息给浏览器（http协议）
            ByteBuf content = Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);
            // 构造一个http的相应，即httpresponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            // 将构建好 response返回
            ctx.writeAndFlush(response);
        }
    }
}
