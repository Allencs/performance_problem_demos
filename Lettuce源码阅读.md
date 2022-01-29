## Lettuce
- lettuce连接共享的原理：底层通信基于netty（底层连接由netty维护），基于通过Future异步机制，获取redis返回数据；

```
RedisClient
StatefulRedisConnection<String, String> connect()

public <K, V> StatefulRedisConnection<K, V> connect(RedisCodec<K, V> codec) {
        checkForRedisURI();
        return getConnection(connectStandaloneAsync(codec, this.redisURI, timeout));
    }

// -------------
private <K, V> ConnectionFuture<StatefulRedisConnection<K, V>> connectStandaloneAsync(RedisCodec<K, V> codec,
            RedisURI redisURI, Duration timeout) {

        assertNotNull(codec);
        checkValidRedisURI(redisURI);

        logger.debug("Trying to get a Redis connection for: " + redisURI);

        DefaultEndpoint endpoint = new DefaultEndpoint(clientOptions, clientResources);
        RedisChannelWriter writer = endpoint;

        if (CommandExpiryWriter.isSupported(clientOptions)) {
            writer = new CommandExpiryWriter(writer, clientOptions, clientResources);
        }

        StatefulRedisConnectionImpl<K, V> connection = newStatefulRedisConnection(writer, codec, timeout);
        ConnectionFuture<StatefulRedisConnection<K, V>> future = connectStatefulAsync(connection, codec, endpoint, redisURI,
                () -> new CommandHandler(clientOptions, clientResources, endpoint));

        future.whenComplete((channelHandler, throwable) -> {

            if (throwable != null) {
                connection.close();
            }
        });



RedisClient.getConnectionBuilder，connectionBuilder基于netty创建和redis服务的物理连接

StatefulRedisConnectionImpl初始化构造函数中创建 1、sync命令执行代理对象 2、async命令执行对象 3、reactive命令执行对象

AbstractRedisAsyncCommands.dispatch() --> StatefulRedisConnectionImpl.dispatch() --> RedisChannelHandler.dispatch() --> DefaultEndpoint.write() --> DefaultEndpoint.writeToChannelAndFlush() --> channelWriteAndFlush()[交给底层netty进行传输，并设置重试监听器]
利用netty ChannelFuture接收返回结果
```