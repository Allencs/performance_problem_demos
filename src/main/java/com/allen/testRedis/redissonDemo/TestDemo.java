package com.allen.testRedis.redissonDemo;

import com.allen.testRedis.redissonDemo.impl.RedisServiceImpl;

public class TestDemo {

    static RedisServiceImpl redisService;

    static {
        redisService = new RedisServiceImpl();
    }

    public static void main(String[] args) {
        redisService.set("allen-test", "this is redisson test");
        redisService.expire("allen-test", 3L);
        System.out.println(redisService.get("allen-test"));
        for (int i = 0; i < 5; i++) {
            redisService.increment("increment", 1L);
        }
        redisService.remove("allen-test");
        redisService.shutdown();
    }


}
