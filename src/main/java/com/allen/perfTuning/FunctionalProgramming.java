package com.allen.perfTuning;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Arrays;

/**
 * 当使用流式编程时，系统需要构造Stream对象，并为流中的每个对象都辅助生成一些管理对象，大量的对象构造导致了非必要的性能损耗。
 * 测试结果如下：
 * Benchmark                                 Mode  Cnt          Score   Error  Units
 * perfTuning.FunctionalProgramming.loop    thrpt    2  197629934.899          ops/s
 * perfTuning.FunctionalProgramming.stream  thrpt    2   18727995.600          ops/s
 */
public class FunctionalProgramming {
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 2)
    @Measurement(iterations = 2)
    public int[] stream() throws IOException {
        int[] data = {1,2,3,4,5,6,7,8,9,10};
        return Arrays.stream(data).map(d->d/2).toArray();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 2)
    @Measurement(iterations = 2)
    public int[] loop() {
        int[] data = {1,2,3,4,5,6,7,8,9,10};
        int size = data.length;
        for(int i=0;i<size;i++) {
            data[i]/=2;
        }
        return data;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(FunctionalProgramming.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }

}
