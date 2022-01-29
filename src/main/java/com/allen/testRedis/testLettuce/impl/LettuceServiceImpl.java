package com.allen.testRedis.testLettuce.impl;

import com.allen.testRedis.RedisService;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

/**
 * @author: allen
 * @date: 2022/1/29 3:53 PM
 * @description:
 **/

public class LettuceServiceImpl implements RedisService {

    private final StatefulRedisConnection<String, String> connection;

    public LettuceServiceImpl() {
        ClientResources clientResources = DefaultClientResources
                .builder()
//                .commandLatencyCollectorOptions( DefaultCommandLatencyCollectorOptions.disabled())
                .build();
        RedisClient redisClient = RedisClient.create(clientResources, RedisURI.create("127.0.0.1", 6379));
        connection = redisClient.connect();
    }

    @Override
    public void set(String key, String value) {
        RedisCommands<String, String> commands = connection.sync();
        commands.set(key, value);
    }

    @Override
    public String get(String key) {
        RedisCommands<String, String> commands = connection.sync();
        String str = commands.get(key);
        return str;
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
