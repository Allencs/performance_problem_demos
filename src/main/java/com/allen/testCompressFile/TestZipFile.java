package com.allen.testCompressFile;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**测试结果
 * zipFileNormal耗时：4468
 * zipFileNormal耗时：3930
 * zipFileNormal耗时：3818
 * zipFileNormal耗时：3662
 * zipFileNormal耗时：3665
 * zipFileNormal耗时：3659
 * zipFileNormal耗时：3671
 * zipFileNormal耗时：3648
 * zipFileNormal耗时：3762
 * zipFileNormal耗时：3642
 *
 * zipFileNormalBytes耗时：117
 * zipFileNormalBytes耗时：114
 * zipFileNormalBytes耗时：118
 * zipFileNormalBytes耗时：116
 * zipFileNormalBytes耗时：120
 * zipFileNormalBytes耗时：113
 * zipFileNormalBytes耗时：111
 * zipFileNormalBytes耗时：115
 * zipFileNormalBytes耗时：124
 * zipFileNormalBytes耗时：113
 *
 * zipFileNormalByteBuffer耗时：3610
 * zipFileNormalByteBuffer耗时：3821
 * zipFileNormalByteBuffer耗时：3578
 * zipFileNormalByteBuffer耗时：3543
 * zipFileNormalByteBuffer耗时：3590
 * zipFileNormalByteBuffer耗时：3756
 * zipFileNormalByteBuffer耗时：3960
 * zipFileNormalByteBuffer耗时：3718
 * zipFileNormalByteBuffer耗时：4004
 * zipFileNormalByteBuffer耗时：3737
 *
 * zipFileWithBuffer耗时：178
 * zipFileWithBuffer耗时：142
 * zipFileWithBuffer耗时：139
 * zipFileWithBuffer耗时：140
 * zipFileWithBuffer耗时：135
 * zipFileWithBuffer耗时：136
 * zipFileWithBuffer耗时：135
 * zipFileWithBuffer耗时：139
 * zipFileWithBuffer耗时：131
 * zipFileWithBuffer耗时：136
 *
 * zipFileWithChannel耗时：137
 * zipFileWithChannel耗时：120
 * zipFileWithChannel耗时：123
 * zipFileWithChannel耗时：125
 * zipFileWithChannel耗时：133
 * zipFileWithChannel耗时：135
 * zipFileWithChannel耗时：127
 * zipFileWithChannel耗时：124
 * zipFileWithChannel耗时：123
 * zipFileWithChannel耗时：120
 *
 * zipFileWithBufferDirectBuffer耗时：149
 * zipFileWithBufferDirectBuffer耗时：151
 * zipFileWithBufferDirectBuffer耗时：153
 * zipFileWithBufferDirectBuffer耗时：154
 * zipFileWithBufferDirectBuffer耗时：146
 * zipFileWithBufferDirectBuffer耗时：171
 * zipFileWithBufferDirectBuffer耗时：160
 * zipFileWithBufferDirectBuffer耗时：166
 * zipFileWithBufferDirectBuffer耗时：171
 * zipFileWithBufferDirectBuffer耗时：157
 *
 * Process finished with exit code 0
 */
public class TestZipFile {
    // 获取桌面绝对路径
    static String homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
    private static String ZIP_FILE = homeDirectory + "/Desktop/pexels-hert-niks.zip";
    private static String JPG_FILE = "jpgFiles/pexels-hert-niks-3224115.jpg";
    private static final ByteBuffer directBuffer = ByteBuffer.allocateDirect(5 * 1024 * 1024);

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
     * 使用read(byte[])的方式，也是会批量字节读取，且效果比Buffer还要好（其实感觉和BufferInputStream的内部buffer缓冲数组一个意思）
     * 其实FileOutputStream只有使用无参read()方法，才会调用read0()的native方法，每次读取1字节，其他都是走readBytes()方法，都是批量读
     * @param dest 目标压缩文件
     * @param src 源文件
     */
    public static void zipFileNormalBytes(String dest, String src) {
        File zipFile = new File(dest);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // 开始
            long startTime = System.currentTimeMillis();
            try (InputStream input = new FileInputStream(src)) {
                zipOut.putNextEntry(new ZipEntry("pexels-hert-niks.jpg"));
                int temp = 0;
                byte[] bytes = new byte[4*1024*1024];
//                byte[] bytes = new byte[1024];
//                input.read(bytes);
//                zipOut.write(bytes);
                while ((temp = input.read(bytes)) != -1) {
                    zipOut.write(bytes, 0, temp);
                }
//                zipOut.write(bytes);

            }
            System.out.println("zipFileNormalBytes耗时：" + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用普通流读写文件（FileInputStream、FileOutputStream），引入了DirectByteBuffer
     * 验证下来，没有啥提升
     * DirectBuffer在这边其实起不到很大效果，因为慢的主要原因是存在read0() native方法的调用
     * @param dest 目标压缩文件
     * @param src 源文件
     */
    public static void zipFileNormalDirectBuffer(String dest, String src) {
        File zipFile = new File(dest);
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            // 开始
            long startTime = System.currentTimeMillis();
            try (InputStream input = new FileInputStream(src)) {
                zipOut.putNextEntry(new ZipEntry("pexels-hert-niks.jpg"));
                int temp = 0;
                // 每次读取1字节
                while ((temp = input.read()) != -1) {
                    directBuffer.put((byte) temp);
//                    zipOut.write(temp);
                }
                directBuffer.flip();
                while(directBuffer.hasRemaining()){
                    zipOut.write(directBuffer.get());
                }
                directBuffer.clear();
            }
            System.out.println("zipFileNormalByteBuffer耗时：" + (System.currentTimeMillis() - startTime));
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

    /**********************************************************************************************
     * 使用缓冲区Buffer读写文件，并引入了DirectByteBuffer【结果显示，反而比不用DirectByteBuffer慢了】
     * 理解：本来DirectByteBuffer出现的意义就是避免了一次从native memory到jvm heap的复制操作，
     * 但是BufferedOutputStream的buffer实际上是heap里的一个byte数组，
     * 因此在读取的时候会把native memory的数据复制到heap的数组（多此一举，又绕回去了）
     * ********************************************************************************************
     * @param dest 目标压缩文件
     * @param src 源文件
     */
    public static void zipFileWithBufferDirectBuffer(String dest, String src) {
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
//                bufferedOutputStream.write(tmp);
                directBuffer.put((byte) tmp);
            }
            directBuffer.flip();
            while(directBuffer.hasRemaining()){
                bufferedOutputStream.write(directBuffer.get());
            }
            directBuffer.clear();
            System.out.println("zipFileWithBufferDirectBuffer耗时：" + (System.currentTimeMillis() - startTime));
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

    public static void testAll() {
        for (int i = 0; i < 10; i++) {
            zipFileNormal(ZIP_FILE, JPG_FILE);
        }
        System.out.println();
        for (int i = 0; i < 10; i++) {
            zipFileNormalBytes(ZIP_FILE, JPG_FILE);
        }
        System.out.println();
        for (int i = 0; i < 10; i++) {
            zipFileNormalDirectBuffer(ZIP_FILE, JPG_FILE);
        }
        System.out.println();
        for (int i = 0; i < 10; i++) {
            zipFileWithBuffer(ZIP_FILE, JPG_FILE);
        }
        System.out.println();
        for (int i = 0; i < 10; i++) {
            zipFileWithChannel(ZIP_FILE, JPG_FILE);
        }
        System.out.println();
        for (int i = 0; i < 10; i++) {
            zipFileWithBufferDirectBuffer(ZIP_FILE, JPG_FILE);
        }
    }

    public static void main(String[] args) {
        testAll();
    }
}