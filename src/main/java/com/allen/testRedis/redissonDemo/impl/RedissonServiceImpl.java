package com.allen.testRedis.redissonDemo.impl;

import com.allen.testRedis.RedisService;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class RedissonServiceImpl implements RedisService {

    private final static Logger log = LoggerFactory.getLogger(RedissonServiceImpl.class);
    private static RedissonClient redissonClient;
    private final static ConcurrentHashMap<String, Object> rBuckets = new ConcurrentHashMap<>();

    public RedissonServiceImpl() {
        try {
            Config config = Config.fromYAML(new File("src/main/resources/redis.yaml"));
            redissonClient = Redisson.create(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> RBucket<T> getRBucket(String key){
        RBucket<T> bucket = (RBucket<T>) rBuckets.get(key);
        if (bucket != null) {
            return bucket;
        }
        bucket = redissonClient.getBucket(key);
        rBuckets.put(key, bucket);
        return bucket;
    }


    @Override
    public void set(String key, String value) {
        RBucket<String> bucket = getRBucket(key);
        bucket.set(value);
    }

    @Override
    public String get(String key) {
        RBucket<String> bucket = getRBucket(key);
        return bucket.get();
    }

    @Override
    public void shutdown() {
        redissonClient.shutdown();
    }

    @Override
    public boolean expire(String key, long expire) {
        RBucket<Object> bucket = getRBucket(key);
        return bucket.expire(expire, TimeUnit.SECONDS);
    }

    @Override
    public void remove(String key) {
        RBucket<String> bucket = getRBucket(key);
        bucket.delete();
    }

    @Override
    public Long increment(String key, long delta) {
        RAtomicLong bucket = redissonClient.getAtomicLong(key);
        return bucket.addAndGet(delta);
    }
}
