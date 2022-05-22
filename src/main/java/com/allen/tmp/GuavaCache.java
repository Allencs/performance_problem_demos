package com.allen.tmp;

import com.allen.localCache.User;
import com.google.common.cache.*;

import java.util.concurrent.TimeUnit;

/**
 * @Author: allen
 * @Date: 2022/5/17 14:50
 * @Description:
 **/
public class GuavaCache {

    //缓存接口这里是LoadingCache，LoadingCache在缓存项不存在时可以自动加载缓存
    private final static LoadingCache<String, User> userCache
            //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
            = CacheBuilder.newBuilder()
            //设置并发级别为8，并发级别是指可以同时写缓存的线程数
            .concurrencyLevel(8)
            //设置写缓存后8秒钟过期
            .expireAfterWrite(8, TimeUnit.SECONDS)
            //设置写缓存后1秒钟刷新
            .refreshAfterWrite(1, TimeUnit.SECONDS)
            //设置缓存容器的初始容量为5
            .initialCapacity(5)
            //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
            .maximumSize(100)
            //设置要统计缓存的命中率
            .recordStats()
            //设置缓存的移除通知
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(RemovalNotification<Object, Object> notification) {
                    System.out.println(notification.getKey() + " 被移除了，原因： " + notification.getCause());
                }
            })
            //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
            .build(
                    new CacheLoader<String, User>() {
                        @Override
                        public User load(String key) throws Exception {
//                            logger.info("缓存没有时，从数据库加载" + key);
                            return new User("tony" + key, key);
                        }
                    }
            );
}
