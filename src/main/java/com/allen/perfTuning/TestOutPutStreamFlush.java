package com.allen.perfTuning;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: allen
 * @Date: 2022/6/22 3:29 PM
 * @Description: 对比I/O立即Flush和Async Flush 性能情况
 */
@State(Scope.Benchmark)
public class TestOutPutStreamFlush {

    private final static String data = "我爱迪丽热巴";
    private File file;
    private FileOutputStream fos;

    @Setup(Level.Trial)
    public void init() {
        this.file = new File("writeFile.log");
        try {
            /* 先检测文件是否存在，如果不存在则先创建*/
            if (!file.exists()) {
                file.createNewFile();
            }
            this.fos = new FileOutputStream(file, true);
            System.out.println("打开文件流...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TearDown(Level.Trial)
    public void check() {
        try {
            this.fos.close();
            System.out.println("关闭文件流...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Benchmark
//    @BenchmarkMode(Mode.Throughput)
//    @Warmup(iterations = 2)
//    @Measurement(iterations = 2)
    public void writeImmediatelyFlush() {
        try {
            byte[] bytesArray = data.getBytes();
            fos.write(bytesArray);
            fos.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 2)
    @Measurement(iterations = 2)
    public void writeFlushAsync() {
        try {
            byte[] bytesArray = data.getBytes();
            fos.write(bytesArray);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestOutPutStreamFlush.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
