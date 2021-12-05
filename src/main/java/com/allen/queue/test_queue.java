package com.allen.queue;

//import com.allen.Tuple;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;


public class test_queue {

    final static ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1000);
    final static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    /**
    public static void main(String[] args) {

        new Thread(() -> {

                for (int i = 0; i < 100; i++) {
                    try {
                        arrayBlockingQueue.put(new Tuple<String, String>("name-" + i, "allen-" + i));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {

                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    Tuple tuple = (Tuple) arrayBlockingQueue.poll(1, TimeUnit.SECONDS);
                    arrayBlockingQueue.size();
                    System.out.println("取得队列：" + tuple.getKey() + " " + tuple.getValue());
                    LockSupport.parkNanos(2000* 1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/
}
