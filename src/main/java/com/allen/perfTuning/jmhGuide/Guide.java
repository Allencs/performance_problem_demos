package com.allen.perfTuning.jmhGuide;

import org.openjdk.jmh.annotations.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class Guide {
    double x;
    List<String> list = new LinkedList<>();
    /**
     * Benchmark标记的方法必须是public的
     * 测量模式：Throughput
     * 吞吐量：表示每秒或每分钟可以执行多少次操作
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureThroughput() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    /**
     * 测量模式：AverageTime
     * 平均时间：表示执行一次操作平均耗时
     */
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureAvgTime() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    /**
     * 测量模式：SampleTime
     * 样本时间：对被测函数进行采样，统计其单次执行时间
     */
    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureSamples() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    /**
     * 测量模式：SingleShotTime
     * 单次执行时间：只对方法进行一次调用，而不对方法进行预热。适用于对方法的冷启动性能测试
     */
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureSingleShot() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    /**
     * 测量模式：混合测量
     * 混合多种测量模式
     */
    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SampleTime, Mode.SingleShotTime})
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureMultiple() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(100);
    }

    /**
     * 对参数进行一些初始化或者释放的操作，就像Spring提供的一些init和destroy方法一样，JHM也提供有这样的钩子
     * @Setup 必须标示在@State注解的类内部，表示初始化操作；
     * @TearDown 必须表示在@State注解的类内部，表示销毁操作；
     * 初始化和销毁的动作都只会执行一次
     *
     * @Setup和@TearDown提供了以下三种纬度的控制:
     * Level.Trial 只会在个基础测试的前后执行。包括Warmup和Measurement阶段，一共只会执行一次。
     * Level.Iteration 每次迭代的时候都会执行，如果Warmup和Measurement都配置了2次执行的话，那么@Setup和@TearDown配置的方法的执行次数就4次。
     * Level.Invocation 每个方法执行的前后执行（一般不推荐这么用）
     */
    @Setup
    public void init() {
        x = Math.PI;
    }

    @TearDown
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    @Benchmark
    public void measureRight() {
        x++;
    }

    /**
     * 编译器可能会将我们的循环进行展开或者做一些其他方面的循环优化，
     * 所以JHM建议我们不要在Benchmark中使用循环，如果我们需要处理循环逻辑了，
     * 可以结合@BenchmarkMode(Mode.SingleShotTime)和@Measurement(batchSize = N)来达到同样的效果
     * ------------------------------------------------------
     * Warmup用来配置预热的内容，可用于类或者方法上，越靠近执行方法的地方越准确。一般配置warmup的参数有这些：
     * iterations：预热的次数。
     * time：每次预热的时间。
     * timeUnit：时间单位，默认是s。
     * batchSize：批处理大小，每次操作调用几次方法。（后面用到）
     * ------------------------------------------------------
     * Measurement
     * 用来控制实际执行的内容，配置的选项本warmup一样。
     * ------------------------------------------------------
     * @return list
     */
    // 每个iteration中做5000次Invocation
    @Benchmark
    @Warmup(iterations = 5, batchSize = 5000)
    @Measurement(iterations = 5, batchSize = 5000)
    @BenchmarkMode(Mode.SingleShotTime)
    public List<String> addList() {
        list.add(list.size() / 2, "something");
        return list;
    }
}
