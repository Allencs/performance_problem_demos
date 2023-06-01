package com.allen.testThreadPool;

import net.openhft.affinity.AffinityLock;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: allen
 * @Date: 2023/6/1 14:21
 * @Description: ScheduledThreadPoolExecutor的一个Bug
 * 当corePoolSize被设置为0的时候【Spring属性值注入失败】，会导致周期性CPU占用达到100%
 * 核心bug存在于java.util.concurrent.ThreadPoolExecutor#getTask，会导致其中的for循环进入死循环
 *
 * JDK 9 中已修复该Bug
 **/
public class ScheduledThreadPoolBug {

    public static void showBug() {
        ScheduledExecutorService e = Executors.newScheduledThreadPool(0);
        e.schedule(() -> {
            System.out.println("业务逻辑");
        }, 60, TimeUnit.SECONDS);
        e.shutdown();
    }

    public static void studyBug() {
        ArrayBlockingQueue<Runnable> workQueue =
                new ArrayBlockingQueue<>(100);
        //绑定到 5 号 CPU 上执行
        try (AffinityLock affinityLock = AffinityLock.acquireLock(5)) {
            for (; ; ) {
                try {
                    Runnable r = workQueue.poll(0, TimeUnit.NANOSECONDS);
                    if (r != null)
                        break;
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public static void main(String[] args){
        showBug();
//        studyBug();
    }
}

