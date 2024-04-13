package com.allen.aboutBuffer.testMappedByteBuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * @Author: allen
 * @Date: 2024/1/30 22:21
 * @Description:
 **/
public class TestMappedByteBuffer {

    public void readFromFileByMappedByteBuffer() throws IOException {
        RandomAccessFile fileAccessor = null;
        FileChannel fileChannel = null;
        try {
            fileAccessor = new RandomAccessFile("/Users/chensheng/tmp/CentOS-7-x86_64-DVD-1708.iso", "r");
            fileChannel = fileAccessor.getChannel();
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0,  Integer.MAX_VALUE);
            for (int i = 0; i < Integer.MAX_VALUE - 1; i++) {
                buffer.get();
            }
            Thread.sleep(600000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            assert fileAccessor != null;
            fileAccessor.close();
            assert fileChannel != null;
            fileChannel.close();
        }

    }

    public static void main(String[] args) {
        TestMappedByteBuffer test = new TestMappedByteBuffer();
        try {
            test.readFromFileByMappedByteBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
