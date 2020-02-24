package com.cll.netty.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyBuf01 {
    public static void main(String[] args) {
        // 创建一个ByteBuf
        // 说明
        // 1. 创建对象 ，该对象包含一个数组arr，是一个byte[10]
        // 2. 在netty的buffer中，不需要使用flip进行反转底层维护了readerIndex和writerIndex
        // 3. 通过readerIndex和writerIndex和capacity，将buffer分成三个区
        // 0-readerIndex 已读取的区
        // readerIndex-writerIndex 可读的区
        // writerIndex- capacity可写的区
        ByteBuf byteBuf = Unpooled.buffer(10);
        for (int i = 0 ; i < 10; i++){
            byteBuf.writeByte(i);

        }
        System.out.println("byte 长度" + byteBuf.capacity());

        for (int i = 0; i< 10 ; i++){
            System.out.println(byteBuf.readByte());
        }
    }

}
