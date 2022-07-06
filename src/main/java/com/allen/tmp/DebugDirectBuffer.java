package com.allen.tmp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;

/**
 * @Author: allen
 * @Date: 2022/7/4 10:26 AM
 * @Description:
 */
public class DebugDirectBuffer {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
//        ByteBuf buf = Unpooled.directBuffer(1024 * 1024);
//        buf.release();
        System.out.println("debug direct memory over");
    }
}
