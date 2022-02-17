package com.allen.testRedis.testLettuce;

import com.allen.testRedis.testLettuce.impl.LettucePoolServiceImpl;
import com.allen.testRedis.testLettuce.impl.LettuceServiceImpl;

/**
 * @author: allen
 * @date: 2022/1/29 3:58 PM
 * @description:
 **/
public class TestLettuce {

    public static void main(String[] args) throws InterruptedException {
        LettuceServiceImpl lettuceService = new LettuceServiceImpl();

        System.out.println(lettuceService.get(Thread.currentThread().getName()));

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                lettuceService.set(Thread.currentThread().getName(), "this is thread " + Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName() + " finished setting value");
                try {
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        }

    }
}
