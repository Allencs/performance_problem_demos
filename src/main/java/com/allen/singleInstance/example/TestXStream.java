package com.allen.singleInstance.example;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class TestXStream {

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public static XStream getInstance() {
//        long start = System.currentTimeMillis();
        XStream instance = new XStream(new Xpp3Driver());
//        System.out.println("foreach spend:"
//                + (System.currentTimeMillis() - start));
        return instance;
    }

    public static void main(String[] args) throws RunnerException {
/*
        int Tel = 87654343;

        for (int i = 0; i < 100; i++) {
            Tel = Tel + i;
            Person p =new Person();
            p.setPersonid("2");
            p.setAddress("上海");
            p.setEmail("54332@qq.com");
            p.setTel(String.valueOf(Tel));
            p.setName("X stream" + Tel);

            System.out.println(XStreamUtils.toXML(p.getClass(), p));
            System.out.println("=======================");
        }

        for (int i = 0; i < 100; i++) {
            Tel = Tel + i;
            NewPerson p =new NewPerson();
            p.setPersonid("2");
            p.setAddress("New York City");
            p.setEmail("54332@qq.com");
            p.setTel(String.valueOf(Tel));
            p.setName("X stream" + Tel);

            System.out.println(XStreamUtils.toXML(p.getClass(), p));
            System.out.println("=======================");
        }

//        for(Object obj: XStreamUtils.xStream.keySet()) {
//            System.out.println(obj);
//            System.out.println(XStreamUtils.xStream.get(obj));
//        }
*/

        Options opt = new OptionsBuilder()
                .include(TestXStream.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
//        TestXStream.getInstance();
    }
}
