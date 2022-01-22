package com.allen.testRedis.testJedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.util.JedisClusterCRC16;
import redis.clients.jedis.util.SafeEncoder;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class RedisCluster extends JedisCluster {

    private static final Logger logger = LoggerFactory.getLogger(RedisCluster.class);

    private static final int DEFAULT_TIMEOUT = 2000;

    private final AtomicReference<RedisClusterNodeStatus> nodeStatus = new AtomicReference<>();

    public RedisCluster(Set<HostAndPort> clusterNodes, JedisPoolConfig jedisPoolConfig) {
        this(clusterNodes, DEFAULT_TIMEOUT, jedisPoolConfig);
    }

    public RedisCluster(Set<HostAndPort> clusterNodes, int timeout, JedisPoolConfig jedisPoolConfig) {
        super(clusterNodes, timeout, jedisPoolConfig);
        nodeStatus.set(new RedisClusterNodeStatus(this.getClusterNodes()));
        Thread nodeStatusUpdater = new Thread(new Runnable(){

            @Override
            public void run() {
                while(true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(DEFAULT_TIMEOUT);
                        nodeStatus.set(new RedisClusterNodeStatus(getClusterNodes()));
                    } catch(Throwable t) {
                        logger.error("update redis cluster node error", t);
                    }
                }
            }

        });
        nodeStatusUpdater.setDaemon(true);
        nodeStatusUpdater.start();
    }

    public RedisClusterPipeline pipelined() {
        return new RedisClusterPipeline();
    }

    private class RedisClusterNodeStatus {

        private Map<String, JedisPool> nodeMap;

        private TreeMap<Long, String> slotHostMap;

        public RedisClusterNodeStatus(Map<String, JedisPool> nodeMap) {
            this.nodeMap = nodeMap;
            this.slotHostMap = getSlotHostMap(nodeMap.keySet().iterator().next());
        }

        @SuppressWarnings("unchecked")
        private TreeMap<Long, String> getSlotHostMap(String anyHostAndPortStr) {
            TreeMap<Long, String> tree = new TreeMap<>();
            String parts[] = anyHostAndPortStr.split(":");
            HostAndPort anyHostAndPort = new HostAndPort(parts[0], Integer.parseInt(parts[1]));
            try {
                Jedis jedis = new Jedis(anyHostAndPort.getHost(), anyHostAndPort.getPort());
                List<Object> clusterSlots = jedis.clusterSlots();
                for (Object clusterSlot : clusterSlots) {
                    List<Object> list = (List<Object>) clusterSlot;
                    List<Object> master = (List<Object>) list.get(2);
                    String hostAndPort = new String((byte[]) master.get(0)) + ":" + master.get(1);
                    tree.put((Long) list.get(0), hostAndPort);
                    tree.put((Long) list.get(1), hostAndPort);
                }
                jedis.close();
            } catch (Exception e) {
                logger.error("get slot host map failed : " + anyHostAndPortStr, e);
            }
            return tree;
        }

        public Map<String, JedisPool> getNodeMap() {
            return nodeMap;
        }

        public TreeMap<Long, String> getSlotHostMap() {
            return slotHostMap;
        }

    }

    public class RedisClusterPipeline extends PipelineBase implements Closeable {

        private final Map<String, Jedis> slotJedisMap = new HashMap<>();

        @Override
        protected Client getClient(String key) {
            return getClient(SafeEncoder.encode(key));
        }

        @Override
        protected Client getClient(byte[] key) {
            Integer slot = JedisClusterCRC16.getSlot(key);
            Map.Entry<Long, String> entry = nodeStatus.get().getSlotHostMap().lowerEntry((long) (slot + 1));
            Jedis jedis = slotJedisMap.get(entry.getValue());
            if (jedis == null) {
                JedisPool jedisPool = nodeStatus.get().getNodeMap().get(entry.getValue());
                jedis = jedisPool.getResource();
                slotJedisMap.put(entry.getValue(), jedis);
            }
            return jedis.getClient();
        }

        public void sync() {
            if (getPipelinedResponseLength() > 0) {
                for (Jedis jedis : slotJedisMap.values()) {
                    for(Object obj : jedis.getClient().getMany(getPipelinedResponseLength())) {
                        generateResponse(obj);
                    }
                }
            }
        }

        @Override
        public void close() {
            for (Jedis jedis : slotJedisMap.values()) {
                jedis.close();
            }
            slotJedisMap.clear();
        }
    }
}


