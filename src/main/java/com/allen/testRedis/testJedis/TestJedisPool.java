package com.allen.testRedis.testJedis;

import com.allen.commons.utils.UuidUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: allen
 * @Date: 2022/7/1 5:42 PM
 * @Description: JedisPoolConfig实际内存dump可能搜不到
 */
public class TestJedisPool {

    private final static JedisPool jedisPool;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // edit by allen 2022.03.18
        jedisPoolConfig.setMaxIdle(50);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);

        jedisPool = new JedisPool(jedisPoolConfig, "10.10.220.15", 6379, 1000, "Perfma888.");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = jedisPool.getResource();
                    jedis.setex( Thread.currentThread().getName(),1, UuidUtil.getStrUuid());
                    System.out.println( Thread.currentThread().getName() + " OK");
                    jedis.close();
                    try {
                        Thread.sleep(120000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
