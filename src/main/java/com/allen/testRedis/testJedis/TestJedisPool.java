package com.allen.testRedis.testJedis;

import cn.hutool.core.util.StrUtil;
import com.allen.utils.DateUtil;
import com.allen.utils.JacksonUtil;
import com.allen.utils.UuidUtil;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        jedisPoolConfig.setMaxIdle(16);
        jedisPoolConfig.setMaxTotal(100);
        jedisPoolConfig.setTestOnBorrow(false);
        jedisPoolConfig.setTestOnReturn(false);

        jedisPool = new JedisPool(jedisPoolConfig, "localhost", 6379, 1000);

//        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
//        jedisClusterNodes.add(new HostAndPort("10.10.220.15", 6379));
//        jedisCluster = new JedisCluster(jedisClusterNodes, 6000, 3000,3, "Perfma888.", jedisPoolConfig);
    }

    public static void setSortedSet() {
        Jedis jedis = jedisPool.getResource();
        for (int loop = 0; loop < 10; loop++) {
            String time = DateUtil.currentTime();
            SortedElement element = new SortedElement();
            element.setName(StrUtil.format("{}-{}", loop, UuidUtil.getStrUuid()));
            element.setTime(time);
            element.setTimeStamp(DateUtil.toMillis(time));
            jedis.zadd("test:sorted:set", element.getTimeStamp(), JacksonUtil.to(element));
        }
    }

    public static void getSortedSet() {
        Jedis jedis = jedisPool.getResource();
        jedis.zremrangeByRank("test:sorted:set", 0, 100);
        Set<String> set = jedis.zrange("test:sorted:set", 0, -1);
        List<SortedElement> list = set.stream().map(element -> JacksonUtil.from(element, SortedElement.class)).collect(Collectors.toList());
        System.out.println(list);
        System.out.println("------------------------------------------");
        Set<String> set1 = jedis.zrevrange("test:sorted:set", 0, -1);
        List<SortedElement> list1 = set1.stream().map(element -> JacksonUtil.from(element, SortedElement.class)).collect(Collectors.toList());
        System.out.println(list1);
    }

    public static void main(String[] args) {
//        for (int i = 0; i < 20; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Jedis jedis = jedisPool.getResource();
//                    jedis.setex(Thread.currentThread().getName(), 1, UuidUtil.getStrUuid());
//                    System.out.println(Thread.currentThread().getName() + " OK");
//                    jedis.close();
//                    try {
//                        Thread.sleep(120000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        setSortedSet();
        getSortedSet();
    }
}
