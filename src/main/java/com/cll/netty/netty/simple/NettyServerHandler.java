package com.cll.netty.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 说明
 * 1. 我们自定义一个Handler 需要继承netty 规定好的某个HandlerAdapter(规范)
 * 2. 这时我们自定义一个Handler，才能称为一个handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    // 读取数据实际（这里我们可以读取客户端发送的消息）
    /**
     *
     * @param ctx 上下文对象，含有管道pipeline，通道channel，地址
     * @param msg 就是客户端发送的数据， 默认Object
     * @throws Exception
     */
    /***
     * 1. 正常程序
     */
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{

        System.out.println("服务器读取线程-----" + Thread.currentThread());
        System.out.println("server ctx = " + ctx);
        // 将msg转成一个ByteBuffer
        // ByteBuf 是Netty提供的（性能更高），不是NIO的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送消息是：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }*/
    /***
     * 2. 要处理长时间才能返回的程序
     */
    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Thread.sleep(10 * 1000);
        System.out.println("----go on ...");
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~2222", CharsetUtil.UTF_8));
    }*/

    /***
     * 3. 任务队列中的Task,用户程序自定义的普通任务
     */
    /*@Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.channel().eventLoop().execute(new Runnable(){

            @Override
            public void run() {
                try {
                    Thread.sleep(10 * 1000);

                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~2222", CharsetUtil.UTF_8));
                }catch (Exception e){
                    System.out.println("出现异常");
                }
            }

        });

        ctx.channel().eventLoop().execute(new Runnable(){

            @Override
            public void run() {
                try {
                    Thread.sleep(20 * 1000);

                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~3333", CharsetUtil.UTF_8));
                }catch (Exception e){
                    System.out.println("出现异常");
                }
            }

        });
        System.out.println("----go on ...");
    }*/


    /***
     * 3. 任务队列中的Task,用户程序自定义定时任务-》 该任务是提交到scheduleTaskQueue中
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.channel().eventLoop().schedule(new Runnable(){

            @Override
            public void run() {
                try {
                    Thread.sleep(10 * 1000);

                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~4444", CharsetUtil.UTF_8));
                }catch (Exception e){
                    System.out.println("出现异常");
                }
            }

        },5 , TimeUnit.SECONDS);

        System.out.println("----go on ...");
    }



    // 数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws  Exception{

        // writeAndFlush 是write + flush
        // 将数据写入到缓存，并刷新
        // 一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello，客户端~", CharsetUtil.UTF_8));
    }

    // 处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
