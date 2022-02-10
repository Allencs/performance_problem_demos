package com.allen.testCompletableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @Author: allen
 * @Date: 2022/2/10 11:43 上午
 * @Description:
 */
public class AcquireResultTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> cp1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello CompletableFuture";
        });

        // getNow，立即获取结果不阻塞，结果计算已完成将返回结果或计算过程中的异常，如果未计算完成将返回设定的valueIfAbsent值
        System.out.println(cp1.getNow("hello No Result"));

        // join 获取结果方法里不会抛异常，但是执行结果会抛异常，抛出的异常为CompletionException
        CompletableFuture<Integer> cp2 = CompletableFuture.supplyAsync((()-> 1 / 0));
        System.out.println(cp2.join());

        // get 抛出ExecutionException
        CompletableFuture<Integer> cp3 = CompletableFuture.supplyAsync((()-> 1 / 0));
        System.out.println(cp3.get());
    }
}
