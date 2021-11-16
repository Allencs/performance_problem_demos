package com.allen.testRedission;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.RunnerException;
import org.redisson.Redisson;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * setValue花费: 5367ms
 * 【hmset，setex】setValueAtLeast花费: 2918ms
 * 【Pipeline】setValueBatch花费: 1220ms
 */
@State(Scope.Benchmark)
public class ClusterServerTester {

    private final static Logger log = LoggerFactory.getLogger(SingleServerTester.class);
    private static RedissonClient redissonClient;

    private static Map<String,String> values = new HashMap<>();
    static RMap<String, String> map;
    static RBucket<String> bucket;

    public static RedissonClient getClient() throws IOException {
        Config config = Config.fromYAML(new File("src/main/resources/redis.yaml"));
        return Redisson.create(config);
    }

    static {
        try {
            redissonClient = getClient();
            log.info("redis连接成功...");
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.put("599055114593", "1");
        values.put("599055114594", "2");

        map = redissonClient.getMap("mall:sale:freq:ctrl:860000000000001");
        bucket = redissonClient.getBucket("mall:total:freq:ctrl:860000000000001");
    }

    public static void setValue() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            map.put("599055114591", "1");
            map.put("599055114592", "2");
            map.expire(3127, TimeUnit.SECONDS);
            bucket.set("3");
            bucket.expire(3127, TimeUnit.SECONDS);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("setValue花费: "+(endTime-startTime) + "ms");
    }

    public static void setValueAtLeast() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            map.putAll(values);
            map.expire(3127, TimeUnit.SECONDS);
            bucket.set("3", 3127, TimeUnit.SECONDS);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("setValueAtLeast花费: "+(endTime-startTime) + "ms");
    }

    public static void setValueBatch() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 500; i++) {
            RBatch batch = redissonClient.createBatch();
            batch.getMap("mall:sale:freq:ctrl:860000000000002").fastPutAsync("599055114593", "3");
            batch.getMap("mall:sale:freq:ctrl:860000000000002").fastPutAsync("599055114594", "4");
            batch.getMap("mall:sale:freq:ctrl:860000000000002").expireAsync(3127, TimeUnit.SECONDS);
            batch.getBucket("mall:total:freq:ctrl:860000000000002").setAsync("3", 3127, TimeUnit.SECONDS);
            batch.execute();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("setValueBatch花费: "+(endTime-startTime) + "ms");
    }

    public static void closeClient(){
        redissonClient.shutdown();
        log.info("成功关闭Redis Client连接");
    }

    public static void main(String[] args) throws RunnerException {
        ClusterServerTester.setValue();
        ClusterServerTester.setValueAtLeast();
        ClusterServerTester.setValueBatch();
//        closeClient();
    }

}
