package com.allen.testStream;

import com.allen.utils.UuidUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: allen
 * @Date: 2022/7/1 1:04 PM
 * @Description:
 */
public class TestParallelStream {

    private static final ArrayList<Cool> arrayList = new ArrayList<>(2000000);

    public TestParallelStream() {

        Random random = new Random();

        for (int i = 0; i < 1000000; i++) {
            Cool cool = new Cool();
            cool.setId(random.nextInt(2000000));
            cool.setCode(UuidUtil.getStrUuid());
            cool.setName(UuidUtil.getStrUuid());
            arrayList.add(cool);
        }

        System.out.println("数据准备完毕");
    }


    public static void main(String[] args) {

        TestParallelStream testParallelStream = new TestParallelStream();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                int count = 0;
                while (count < 1000) {
                    List<Cool> n1List, n2List;
                    n1List = arrayList.parallelStream()
                            .sorted(Comparator.comparing(Cool::getId))
                            .collect(Collectors.toList());

//                    n2List = arrayList.parallelStream()
//                            .sorted(Comparator.comparing(Cool::getCode))
//                            .collect(Collectors.toList());

                    System.out.println(n1List.get(0));
//                    System.out.println(n2List.get(0));
                    count++;
                }
            });
        }


    }
}
