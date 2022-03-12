package com.allen.testFuture.testCompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Author: allen
 * @Date: 2022/3/12 10:01 PM
 * @Description: CompletableFuture实现泡茶流程
 **/
public class MakeTeaDemo {

    private static final Logger logger = LoggerFactory.getLogger(MakeTeaDemo.class);

    public static void makeTea() {
        //任务1：洗水壶->烧开水
        CompletableFuture<Void> f1 = CompletableFuture
                .runAsync(() -> {
                    Random random = new Random();
                    int num = random.nextInt(3);
                    logger.info("T1:洗水壶...");
                    if (num % 2 == 0) {
                        sleep(1, TimeUnit.SECONDS);
                    } else {
                        throw new RuntimeException("水壶打碎了");
                    }
                    logger.info("T1:烧开水...");
                    sleep(10, TimeUnit.SECONDS);
                });
        //任务2：洗茶壶->洗茶杯->拿茶叶
        CompletableFuture<String> f2 = CompletableFuture
                .supplyAsync(() -> {
                    logger.info("T2:洗茶壶...");
                    sleep(1, TimeUnit.SECONDS);

                    logger.info("T2:洗茶杯...");
                    sleep(2, TimeUnit.SECONDS);

                    logger.info("T2:拿茶叶...");
                    sleep(1, TimeUnit.SECONDS);
                    return "龙井";
                });
        //任务3：任务1和任务2完成后执行：泡茶
        CompletableFuture<String> f3 = f1.thenCombine(f2,
                        (unused, teaName) -> "上茶：" + teaName)
                .exceptionally(new Function<Throwable, String>() {
                    @Override
                    public String apply(Throwable throwable) {
                        logger.error(throwable.getMessage());
                        return "T1:买新水壶";
                    }
                });
        // 等待任务3执行结果
        logger.info(f3.join());
        // 获取任务3结果
        f3.whenComplete((s, throwable) ->
        {
            if (!s.contains("龙井")) {
            logger.info("出事故了，茶没得喝了");
        }
        else {
            logger.info("上茶结束 | 播放音乐");
        }
        });
    }

    static void sleep(int t, TimeUnit u) {
        try {
            u.sleep(t);
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args) {
        makeTea();
    }
}
