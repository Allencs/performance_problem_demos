package com.allen.testRedis.testJedis;

import com.allen.utils.UuidUtil;
import redis.clients.jedis.Jedis;

public class TestJedis {

    public static void main(String[] args) {
        //权限认证
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = new Jedis("localhost", 6379);
                    jedis.set( Thread.currentThread().getName(), UuidUtil.getStrUuid());
                    System.out.println( Thread.currentThread().getName() + " OK");
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        for (int i = 0; i < 20; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = new Jedis("localhost", 6379);
                    jedis.set( Thread.currentThread().getName(), UuidUtil.getStrUuid());
                    System.out.println( Thread.currentThread().getName() + " OK");
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
