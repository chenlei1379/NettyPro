package com.cll.netty.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public class NettyBuf02 {

    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world", Charset.forName("utf-8"));
        // 相关使用方法
        if(byteBuf.hasArray()){
            byte[] content = byteBuf.array();
            // 将content转成字符串
            System.out.println(new String(content, Charset.forName("utf-8")));

            System.out.println("byteBuf=" + byteBuf);

            System.out.println(byteBuf.arrayOffset());
            System.out.println(byteBuf.readerIndex());
            System.out.println(byteBuf.writerIndex());
            System.out.println(byteBuf.capacity());
            System.out.println(byteBuf.readByte());
            int len = byteBuf.readableBytes(); // 可读的字符数
            System.out.println(len);
            for (int i = 0; i < len; i++) {
                System.out.println((char)byteBuf.readByte());
            }

            System.out.println(byteBuf.readerIndex());

            // 按照某个范围读取
            System.out.println(byteBuf.getCharSequence(0, 4, Charset.forName("utf-8")));
            System.out.println(byteBuf.getCharSequence(4, 6, Charset.forName("utf-8")));
        }

    }
}
