package com.cll.netty.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.Socket;

public class NettyServer {

    public static void main(String[] args) throws Exception{

        // 创建BossGroup 和 WorkerGroup
        //说明
        // 1. 创建两个线程组bossGroup 和 workerGroup
        // 2. bossGroup 只是处理连接请求， 真正的和客户端业务处理，会交给workerGroup完成
        // 3. 两个都是无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
       try {


           // 创建服务器端的启动对象， 配置参数
           ServerBootstrap bootstrap = new ServerBootstrap();

           // 使用链式编程来进行设置
           bootstrap.group(bossGroup,workerGroup) // 设置两个线程组
                   .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel作为服务器的通道实现
                   .option(ChannelOption.SO_BACKLOG,128) // 设置线程队列得到连接个数
                   .childOption(ChannelOption.SO_KEEPALIVE,true) // 设置保持活动连接状态
                   .childHandler(new ChannelInitializer<SocketChannel>() { // 创建一个通道初始化对象（匿名对象）
                       // 给pipeline设置处理器
                       @Override
                       protected  void initChannel(SocketChannel cl) throws Exception{
                            cl.pipeline().addLast(new NettyServerHandler());
                       }

                   }); // 给我们的workerGroup的 EventLoop 对应的管道设置处理器

           System.out.println("...服务器is ready...");
           // 绑定一个端口并且同步，生成了一个ChannelFuture对象
           // 启动服务器（并绑定端口）
           ChannelFuture cf = bootstrap.bind(6668).sync();
           cf.addListener(new ChannelFutureListener() {
               @Override
               public void operationComplete(ChannelFuture channelFuture) throws Exception {
                   if(channelFuture.isSuccess()){
                       System.out.println("-------------成功");
                   }else{
                       System.out.println("-------------失败");
                   }
               }
           });
           // 对关闭通道进行监听
           cf.channel().closeFuture().sync();
       }finally {
           bossGroup.shutdownGracefully(); // 优雅的关闭
           workerGroup.shutdownGracefully();// 优雅的关闭
       }
    }
}














