package com.allen.testRedis.testLettuce.impl;

import com.allen.testRedis.RedisService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

/**
 * @author: allen
 * @date: 2022/1/30 10:56 AM
 * @description:
 **/
public class LettucePoolServiceImpl implements RedisService {

    private final GenericObjectPool pool;

    public LettucePoolServiceImpl() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(10);
        config.setMinIdle(10);
        config.setMaxTotal(20);
        RedisClient client = RedisClient.create(RedisURI.create("127.0.0.1", 6379));
        pool = ConnectionPoolSupport
                .createGenericObjectPool(client::connect, config);
    }

    public StatefulRedisConnection<String, String> getConnection() throws Exception {
        return (StatefulRedisConnection<String, String>) pool.borrowObject();
    }

    @Override
    public void set(String key, String value) {
        RedisCommands<String, String> commands;
        try {
            commands = getConnection().sync();
            commands.set(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String key) {
        RedisCommands<String, String> commands = null;
        try {
            commands = getConnection().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert commands != null;
        return commands.get(key);
    }

    @Override
    public boolean expire(String key, long expire) {
        return false;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public Long increment(String key, long delta) {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
