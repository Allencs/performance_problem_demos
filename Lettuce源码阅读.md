## Lettuce
三大模块RedisClient、Connection、RedisCommands

### RedisClient
- 创建Redis客户端（uri，port，timeout），创建底层netty相关对象

1. 构建RedisURI，主要代码在RedisURI类中
2. 创建RedisClient客户端，核心代码AbstractRedisClient类，
    1. 设置客户端资源对象（客户端各种参数配置）
    2. 在构建DefaultClientResources时创建底层netty客户端

### Connection
- 创建底层netty Channel，构建和redis服务的tcp连接
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

- lettuce创建连接过程

RedisClient.connect -> RedisClient.connectStandaloneAsync【new DefaultEndpoint()[创建一个closeFuture，CompletableFuture对象] -> RedisClient.newStatefulRedisConnection[（StatefulRedisConnectionImpl继承RedisChannelHandler）RedisChannelHandler. -> 初始化四个组件
        this.codec = codec;
        this.async = newRedisAsyncCommandsImpl();
        this.sync = newRedisSyncCommandsImpl(); // 代理对象
        this.reactive = newRedisReactiveCommandsImpl();
        ] -> 新建ConnectionFuture connectStatefulAsync【初始化CommandHandler（继承于ChannelDuplexHandler，属于netty类） -> RedisClient.getConnectionBuilder构建新的ConnectionBuilder 
-> AbstractRedisClient.connectionBuilder(新建netty客户端) -> connectionBuilder.connection将StatefulRedisConnectionImpl设置到ConnectionBuilder的connection属性 
-> AbstractRedisClient.initializeChannelAsync「新建socketAddressFuture和channelReadyFuture，都是CompletableFuture类型 -> 返回新建的DefaultConnectionFuture」】】 -> AbstractRedisClient.getConnection

- lettuce发送一条命令的执行流程
以commands.set(key, value)同步执行为例：
io.lettuce.core.internal.AbstractInvocationHandler.invoke -> FutureSyncInvocationHandler.handleInvocation -> AbstractRedisAsyncCommands.set【返回RedisFuture类型对象】 -> RedisCommandBuilder.set【构建redis命令对象】 
-> AbstractRedisAsyncCommands.dispatch() 【new AsyncCommand<>(cmd)，将普通Command对象封装成AsyncCommand对象】 -> StatefulRedisConnectionImpl.dispatch() 
-> StatefulRedisConnectionImpl.preProcessCommand【1、首先判断是否需要安全验证 2、是否选择自定义库 3、是否只读模式 4、是否读写模式 5、是否DISCARD 6、是否为EXEC 7、是否为MULTI】 
-> RedisChannelHandler.dispatch()【判断是否为debug或者tracingEnabled（默认false）】 -> DefaultEndpoint.write() -> DefaultEndpoint.writeToChannelAndFlush() 
-> DefaultEndpoint.channelWriteAndFlush()[交给底层netty进行传输，并设置重试监听器] -> [netty部分]AbstractChannel.writeAndFlush
利用netty ChannelFuture接收返回结果
```


### RedisCommands
以上面发送set命令为例：
```Command命令对象
Command(ProtocolKeyword type, CommandOutput<K, V, T> output, CommandArgs<K, V> args) {
        LettuceAssert.notNull(type, "Command type must not be null");
        this.type = type; // SET
        this.output = output; // StatusOutput
        this.args = args; // 实际发送内容：[buffer=$8Thread-2$23this is thread Thread-2]
    }
```
