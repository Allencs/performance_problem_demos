package com.allen.testStream;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(Main.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2)
    @Measurement(iterations = 2)
    public static Map<String, Cool> extracted1() {
        List<Cool> cools = new ArrayList<>(200_0000);

        Random random = new Random(1000);
        for (int i = 0; i < 100_0000; i++) {
            Cool cool = new Cool();
            cool.setCc(""+ random.nextInt());
            cool.setPp(""+ random.nextInt());
            cool.setName(""+ i);
            cool.setCode(""+ random.nextInt());
            cools.add(cool);
        }

//        Map<String, Cool> collect = cools.stream().collect(Collectors.toMap(Cool::getName, u -> u, (o1,o2)->{
//            if (o1.getName().equals(o2.getName())){
//                return o1;
//            }
//            return o2;
//        },HashMap::new));
        Map<String, Cool> collect = cools.stream()
                .filter(u-> Integer.parseInt(u.getName())%10!=0)
                .collect(Collectors.toMap(Cool::getName, u -> u));
        return collect;
//        System.out.println(collect);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 2)
    @Measurement(iterations = 2)
    public static Map<String, Cool> extracted2() {
        List<Cool> cools = new ArrayList<>(200_0000);

        Random random = new Random(1000);
        for (int i = 0; i < 100_0000; i++) {
            Cool cool = new Cool();
            cool.setCc(""+ random.nextInt());
            cool.setPp(""+ random.nextInt());
            cool.setName(""+ i);
            cool.setCode(""+ random.nextInt());
            cools.add(cool);
        }
        // write your code here
        int size = cools.size();
        HashMap<String, Cool> collect = new HashMap<>();
        for (int i = 0; i < size; i++) {
            if (Integer.parseInt(cools.get(i).getName())%10 !=0) {
                collect.put(cools.get(i).getName(),cools.get(i));
            }

        }
//        System.out.println(collect);
        return collect;
    }

}
