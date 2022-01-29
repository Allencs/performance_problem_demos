package com.allen.testRedis.testLettuce;

import com.allen.testRedis.testLettuce.impl.LettuceServiceImpl;

/**
 * @author: allen
 * @date: 2022/1/29 3:58 PM
 * @description:
 **/
public class TestLettuce {

    public static void main(String[] args) {
        LettuceServiceImpl lettuceService = new LettuceServiceImpl();

        lettuceService.get(Thread.currentThread().getName());
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> lettuceService.set(Thread.currentThread().getName(), "this is thread " + Thread.currentThread().getName())).start();
//        }
    }
}
