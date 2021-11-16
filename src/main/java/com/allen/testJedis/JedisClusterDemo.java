package com.allen.testJedis;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Benchmark                                           Mode  Cnt  Score   Error  Units
 * testJedis.JedisClusterDemo.setValue                 avgt    2  3.343           s/op
 * testJedis.JedisClusterDemo.setValueBatch            avgt    2  2.001           s/op
 * testJedis.JedisClusterDemo.setValuePipeLine         avgt    2  1.340           s/op
 * testJedis.JedisClusterDemo.setValuePipeLineHashTag  avgt    2  0.671           s/op
 */

@State(Scope.Benchmark)
public class JedisClusterDemo {

    private static JedisCluster jedisCluster;
    private static RedisCluster redisCluster;
    private static Map<String,String> map = new HashMap<>();
    private static final RedisCluster.RedisClusterPipeline pipe;

    static {
        // 添加集群的服务节点Set集合
        Set<HostAndPort> hostAndPortsSet = new HashSet<>();
        // 添加节点
        hostAndPortsSet.add(new HostAndPort("172.16.16.212", 6371));

        // Jedis连接池配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲连接数, 默认8个
        jedisPoolConfig.setMaxIdle(16);
        // 最大连接数, 默认8个
        jedisPoolConfig.setMaxTotal(16);
        //最小空闲连接数, 默认0
        jedisPoolConfig.setMinIdle(16);
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        jedisPoolConfig.setMaxWaitMillis(2000); // 设置2秒
        //对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(false);

        jedisCluster = new JedisCluster(hostAndPortsSet, jedisPoolConfig);
        redisCluster = new RedisCluster(hostAndPortsSet, jedisPoolConfig);
        pipe =  redisCluster.pipelined();

        map.put("599055114593", "1");
        map.put("599055114594", "2");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, batchSize = 500)
    public static void setValue() {
        //System.out.println("清空数据："+jedis.flushDB());
        jedisCluster.hset("mall:sale:freq:ctrl:860000000000001", "599055114591", "1");
        jedisCluster.hset("mall:sale:freq:ctrl:860000000000001", "599055114592", "2");
        jedisCluster.expire("mall:sale:freq:ctrl:860000000000001", 3127);
        jedisCluster.set("mall:total:freq:ctrl:860000000000001", "3");
        jedisCluster.expire("mall:total:freq:ctrl:860000000000001", 3127);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, batchSize = 500)
    public static void setValueBatch() {
        jedisCluster.hmset("mall:sale:freq:ctrl:860000000000001", map);
        jedisCluster.expire("mall:sale:freq:ctrl:860000000000001", 3127);
        jedisCluster.setex("mall:total:freq:ctrl:860000000000001",3127, "3");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, batchSize = 500)
    public static void setValuePipeLine() {
        pipe.hmset("mall:sale:freq:ctrl:860000000000001", map);
        pipe.expire("mall:sale:freq:ctrl:860000000000001", 3127);
        //将封装后的PIPE一次性发给redis
        pipe.sync();
        redisCluster.setex("mall:total:freq:ctrl:860000000000001",3127, "3");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 1)
    @Measurement(iterations = 2, batchSize = 500)
    public static void setValuePipeLineHashTag() {
        pipe.hmset("mall:sale:freq:ctrl:{860000000000001}", map);
        pipe.expire("mall:sale:freq:ctrl:{860000000000001}", 3127);
        pipe.setex("mall:total:freq:ctrl:{860000000000001}",3127, "3");
        //将封装后的PIPE一次性发给redis
        pipe.sync();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JedisClusterDemo.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
//    setValuePipeLine();
    }
}

