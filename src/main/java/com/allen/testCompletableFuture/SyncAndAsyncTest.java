package com.allen.testCompletableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Author: allen
 * @Date: 2022/2/10 1:53 下午
 * @Description:
 */
public class SyncAndAsyncTest {

    private static List<Shop> shopList = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll")
    );

    private static List<String> findPriceSync(String product) {
        return shopList.stream()
                .map(shop -> String.format("%s price is %.2f",
                        shop.getName(), shop.getPrice(product)))  //格式转换
                .collect(Collectors.toList());
    }

    private static List<String> findPriceAsync(String product) {
        List<CompletableFuture<String>> completableFutureList = shopList.stream()
                //转异步执行
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> String.format("%s price is %.2f",
                                shop.getName(), shop.getPrice(product))))  //格式转换
                .collect(Collectors.toList());

        return completableFutureList.stream()
                .map(CompletableFuture::join)  //获取结果不会抛出异常
                .collect(Collectors.toList());
    }

    /**
     * Sync耗时: 4075
     * Async耗时: 1011
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println(findPriceSync("Sync-Test"));
        System.out.println("Sync耗时: " + (System.currentTimeMillis() - startTime));

        long startTime2 = System.currentTimeMillis();
        findPriceAsync("Async-Test");
        System.out.println("Async耗时: " + (System.currentTimeMillis() - startTime2));
    }
}
