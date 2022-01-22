package com.allen.testRedis.testRedission;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SingleServerTester {
    private final static Logger log = LoggerFactory.getLogger(SingleServerTester.class);
    private RedissonClient redissonClient;

    public SingleServerTester() throws IOException {
        Config config = Config.fromYAML(new File("src/main/resources/redis.yaml"));
        redissonClient = Redisson.create(config);
    }

    public void testRedission() {
        RLock redissonLock = redissonClient.getLock("testLock");
        try {
//        RBucket<String> bucket = redissonClient.getBucket("name");
            redissonLock.lock(30, TimeUnit.SECONDS);
            log.info("{}进入redisson分布式锁的接口中",Thread.currentThread().getName());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            redissonLock.unlock();
        }

    }

    public void testRedission2() {
        try {
            RBucket<String> bucket = redissonClient.getBucket(Thread.currentThread().getName());
            bucket.set("test-redisson");
            log.info(String.format("%s - 设置完毕", Thread.currentThread().getName()));
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        SingleServerTester redissionTester = new SingleServerTester();
        Thread.sleep(120000);
//        ExecutorService executorService = new ThreadPoolExecutor(50, 100, 0L,
//                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
//        int i = 0;
//        while (i < 100000) {
//            executorService.submit(redissionTester::testRedission2);
//            i++;
//        }
    }


}
