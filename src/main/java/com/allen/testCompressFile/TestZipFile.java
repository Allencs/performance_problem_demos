package com.allen.testCompressFile;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestZipFile {

    private static String ZIP_FILE = "/Users/chensheng/Desktop/pexels-hert-niks.zip";
    private static String JPG_FILE = "/Users/chensheng/Desktop/jpegFiles/pexels-hert-niks-3224115.jpg";

    /**
     * 使用普通流读写文件（FileInputStream、FileOutputStream）
     * FileInputStream的read方法每次只会读取1字节内容，即4M文件大约需要操作4 * 1024 * 1024次native方法
     * @param dest 目标压缩文件
     * @param src 源文件
     */
    public static void zipFileNormal(String dest, String src) {
        File zipFile = new File(dest);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // 开始
            long startTime = System.currentTimeMillis();
            try (InputStream input = new FileInputStream(src)) {
                zipOut.putNextEntry(new ZipEntry("pexels-hert-niks.jpg"));
                int temp = 0;
                // 每次读取1字节
                while ((temp = input.read()) != -1) {
                    zipOut.write(temp);
                }
            }
            System.out.println("zipFileNormal耗时：" + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用缓冲区Buffer读写文件（BufferedOutputStream、BufferedInputStream）
     * 借助缓冲区，可以一次性将文件读取至Buffer
     * @param dest 目标压缩文件
     * @param src 源文件
     */
    public static void zipFileWithBuffer(String dest, String src) {
        File zipFile = new File(dest);
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOut);
            // 开始
            long startTime = System.currentTimeMillis();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(src));
            zipOut.putNextEntry(new ZipEntry("pexels-hert-niks.jpg"));
            int tmp = 0;
            while ((tmp = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(tmp);
            }
            System.out.println("zipFileWithBuffer耗时：" + (System.currentTimeMillis() - startTime));
            // 遇到了流没关闭导致的zip文件损坏，close会在真正关闭之前，强制刷新缓冲区，将数据写入目标位置
            bufferedOutputStream.close();
            bufferedInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用NIO Channel进行文件读写
     * transferTo利用零拷贝，避免了磁盘 -> 内核缓冲 -> 用户缓冲，而是直接在内核缓冲区进行传输，底层利用Linux的sendfile接口；
     * @param dest 目标压缩文件
     * @param src 源文件
     */
    public static void zipFileWithChannel(String dest, String src) {
        File zipFile = new File(dest);
        try {
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            // 开始
            long startTime = System.currentTimeMillis();
            WritableByteChannel writableByteChannel = Channels.newChannel(zipOut);
            FileChannel fileChannel = new FileInputStream(src).getChannel();
            zipOut.putNextEntry(new ZipEntry("pexels-hert-niks.jpg"));
//            fileChannel.transferTo(0, fileChannel.size(), writableByteChannel);
            long size = fileChannel.size();
            long postion = 0 ;
            while (size > 0) {
            // 循环写数据，避免数据丢失（单次8M）
                long count = fileChannel.transferTo(postion, size, writableByteChannel);
                postion += count;
                size -= count;
            }
            System.out.println("zipFileWithChannel耗时：" + (System.currentTimeMillis() - startTime));
            fileChannel.close();
            writableByteChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        zipFileNormal(ZIP_FILE, JPG_FILE);
        zipFileWithBuffer(ZIP_FILE, JPG_FILE);
//        zipFileWithChannel(ZIP_FILE, JPG_FILE);
    }
}