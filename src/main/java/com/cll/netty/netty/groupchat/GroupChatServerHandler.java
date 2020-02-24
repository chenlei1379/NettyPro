package com.cll.netty.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

public class GroupChatServerHandler extends SimpleChannelInboundHandler {

    // 定义一个channel组，管理所有的channel
    // GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // handlerAdded 表示连接建立，一旦连接，第一被执行
    // 将当前channel加入到channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将该客户加入聊天的信息推送给其它在线的客户端
        /**
         * 该方法会将channelGroup中所有的channel遍历,并发送消息
         * 我们不要自己遍历
         */
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天\n");
        channelGroup.add(channel);
    }

    /**
     * 断开连接，将xx客户离开信息推送给当前在线客户
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]"+channel.remoteAddress()+"离开了");
        System.out.println("channelGroup size" + channelGroup.size());
    }

    /**
     * 表示channel处于活动状态，提示xx上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"上线了~");
    }

    // 表示channel处于不活动状态，提示xx离线了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"离线了！~");
    }
    // 读取数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.forEach(ch -> {
            if(channel != ch){
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + msg.toString() + "\n");
            }else{
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + msg.toString() + "\n");
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close(); // 关闭通道
    }
}
