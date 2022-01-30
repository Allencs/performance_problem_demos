package com.allen.testRedis.testLettuce;

import com.allen.testRedis.testLettuce.impl.LettucePoolServiceImpl;

/**
 * @author: allen
 * @date: 2022/1/30 11:25 AM
 * @description:
 **/
public class TestLettucePool {
    public static void main(String[] args) {
        LettucePoolServiceImpl lettucePoolService = new LettucePoolServiceImpl();
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                lettucePoolService.set(Thread.currentThread().getName(), "this is thread " + Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName() + " finished setting value");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }
}
