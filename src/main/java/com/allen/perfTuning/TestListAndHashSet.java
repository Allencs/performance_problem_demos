package com.allen.perfTuning;

import com.allen.utils.UuidUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * @Author: allen
 * @Date: 2022/2/16 5:45 下午
 * @Description: 对比ArrayList和HashSet各种场景下判断元素是否存在的性能情况
 *
 * 测试结果：
 * Benchmark                                                  Mode  Cnt       Score      Error   Units
 * perfTuning.TestListAndHashSet.checkArrayListWithContains  thrpt    5      24.929 ±    9.703  ops/ms
 * perfTuning.TestListAndHashSet.checkArrayListWithIndex     thrpt    5      25.505 ±    1.811  ops/ms
 * perfTuning.TestListAndHashSet.checkArrayListWithIterator  thrpt    5      23.496 ±    3.172  ops/ms
 * perfTuning.TestListAndHashSet.checkHashSet                thrpt    5  191505.153 ± 9573.444  ops/ms
 */

@State(Scope.Benchmark)
public class TestListAndHashSet {

    public static final HashSet<String> hashSet = new HashSet<String>();
    public static final ArrayList<String> arrayList = new ArrayList<String>();

    /**
     * 构建测试集合HashSet和ArrayList，都存放10001个元素，并将待判断元素添加至尾部
     */
    @Setup(Level.Trial)
    public void init() {
        for (int i = 0; i < 10000; i++) {
            hashSet.add(UuidUtil.getStrUuid());
        }
        hashSet.add("hashSet-test");

        for (int i = 0; i < 10000; i++) {
            arrayList.add(UuidUtil.getStrUuid());
        }
        arrayList.add("arrayList-test");
    }

    /**
     * HashSet通过定位key的hash值进行查找，时间复杂度O(1)
     * @return Boolean
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Boolean checkHashSet() {
        return hashSet.contains("hashSet-test");
    }

    /**
     * 通过迭代器遍历ArrayList进行逐个比对，时间复杂度O(n)
     * @return Boolean
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Boolean checkArrayListWithIterator() {
        for (String s : arrayList) {
            if (s.equals("arrayList-test")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过index手工遍历ArrayList进行逐个比对，时间复杂度O(n)
     * @return Boolean
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Boolean checkArrayListWithIndex() {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).equals("arrayList-test")) {
                return true;
            }
        }
        return false;
    }

    /**
     * ArrayList的contains方法通过遍历list进行逐个比对，时间复杂度O(n)
     * @return Boolean
     */
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public Boolean checkArrayListWithContains() {
        return arrayList.contains("arrayList-test");
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TestListAndHashSet.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
