package com.allen.testIntern;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author: allen
 * @Date: 2022/9/23 13:23
 * @Description:
 * -XX:+PrintGCDetails观察GC情况
 * 区别于-XX:+UseStringDeduplication字符串去重功能，此能力只有在G1垃圾收集器下才适用
 */
public class Perf_StringIntern {
    public static ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(10000000);

    public static void generateString() {
        Integer[] data = new Integer[10];
        Random random = new Random(10 * 10000);
        for (int i = 0; i< data.length; i++){
            data[i] = random.nextInt();
        }
        for (int i =0; i < 10000000; i++) {
            try {
//                arrayBlockingQueue.put(String.valueOf(data[i % data.length]));
                arrayBlockingQueue.put(String.valueOf(data[i % data.length]).intern());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            System.out.println("创建完毕");
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        Perf_StringIntern.generateString();
    }
}
