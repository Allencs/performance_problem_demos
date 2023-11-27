package com.allen.testRedis.testJedis;

import com.allen.utils.UuidUtil;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: allen
 * @Date: 2022/7/1 5:42 PM
 * @Description: JedisPoolConfig实际内存dump可能搜不到
 */
public class TestJedisPool {

    private final static JedisPool jedisPool;

//    private final static JedisCluster jedisCluster;

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // edit by allen 2022.03.18
        jedisPoolConfig.setMaxIdle(50);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);

        jedisPool = new JedisPool(jedisPoolConfig, "10.10.220.15", 6379, 1000, "Perfma888.");

        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        jedisClusterNodes.add(new HostAndPort("10.10.220.15", 6379));
//        jedisCluster = new JedisCluster(jedisClusterNodes, 6000, 3000,3, "Perfma888.", jedisPoolConfig);
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
