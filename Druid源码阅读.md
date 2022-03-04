## Druid 连接检测导致的频繁类加载

### `testConnectionInternal`连接测试的核心方法
在`com.alibaba.druid.pool.DruidAbstractDataSource.testConnectionInternal`中，会调用`getLastPacketReceivedTimeMs`方法，在
`getLastPacketReceivedTimeMs`方法中，就会进行类加载操作；


### `getLastPacketReceivedTimeMs`方法
获取连接离最后一次接收数据包的间隔时间。如果使用`mysql-connector-java 6`以上，底层调用的是`com.mysql.cj.jdbc.JdbcConnection`的`getIdleFor`方法，
即连接的空闲时长。

```java
/**
 * druid 1.1.23版本之前的源码实现
 * 如果配置了自定义Filter，这边class_connectionImpl就是null
 */
public static long getLastPacketReceivedTimeMs(Connection conn) throws SQLException {
        if (class_connectionImpl == null && !class_connectionImpl_Error) {
        try {
            // 写死mysql-connector-java 5的类
            // 因此使用6+版本的驱动，会存在ClassNotFound的问题。
            class_connectionImpl = Utils.loadClass("com.mysql.jdbc.MySQLConnection");
        } catch (Throwable error){
        class_connectionImpl_Error = true;
        }
        }
}

/**
 * druid 1.1.23版本及之后的源码实现
 * @param conn ：如果配置了自定义Filter，传入的conn就是ConnectionProxyImpl类型，否则就是ConnectionImpl类型
 */
public static long getLastPacketReceivedTimeMs(Connection conn) throws SQLException {
        // 如果配置了自定义Filter，这边class_connectionImpl就是null
        if (class_connectionImpl == null && !class_connectionImpl_Error) {
            try {
                // 加载mysql连接类
                class_connectionImpl = Utils.loadClass("com.mysql.jdbc.MySQLConnection");
                if (class_connectionImpl == null) {
                    class_connectionImpl = Utils.loadClass("com.mysql.cj.MysqlConnection");
                    if (class_connectionImpl != null) {
                        mysqlJdbcVersion6 = true;
                    }
                }
            } catch (Throwable error) {
                class_connectionImpl_Error = true;
            }
        }
        .....
      }
```

```java
public class TomcatEmbeddedWebappClassLoader extends ParallelWebappClassLoader {

 public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
  synchronized (JreCompat.isGraalAvailable() ? this : getClassLoadingLock(name)) {
      // 会先查找缓存
   Class<?> result = findExistingLoadedClass(name);
   result = (result != null) ? result : doLoadClass(name);
   if (result == null) {
    throw new ClassNotFoundException(name);
   }
   return resolveIfNecessary(result, resolve);
  }
 }
}
```

### 关于代理连接对象的创建
如果配置了druid的`Filter`，就会创建代理连接对象`ConnectionProxyImpl`。

核心调用逻辑如下（⚠️初始化连接创建的时候进行）：
1. com.alibaba.druid.pool.DruidDataSource#getConnection()

2. com.alibaba.druid.pool.DruidDataSource#getConnection(long)

3. com.alibaba.druid.pool.DruidDataSource#init

4. com.alibaba.druid.pool.DruidAbstractDataSource#createPhysicalConnection()

5. com.alibaba.druid.pool.DruidAbstractDataSource#createPhysicalConnection(java.lang.String, java.util.Properties)
```java
// 如果添加了过滤器，就会调用FilterChainImpl的connection_connect方法
public Connection createPhysicalConnection(String url, Properties info) throws SQLException {
Connection conn;
if (getProxyFilters().size() == 0) {
conn = getDriver().connect(url, info);
} else {
conn = new FilterChainImpl(this).connection_connect(info);
}

        createCountUpdater.incrementAndGet(this);

        return conn;
    }
```

6. com.alibaba.druid.filter.FilterChainImpl#connection_connect
```java
public ConnectionProxy connection_connect(Properties info) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_connect(this, info);
        }

        Driver driver = dataSource.getRawDriver();
        String url = dataSource.getRawJdbcUrl();

        Connection nativeConnection = driver.connect(url, info);

        if (nativeConnection == null) {
            return null;
        }

        return new ConnectionProxyImpl(dataSource, nativeConnection, info, dataSource.createConnectionId());
    }
```

7. com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl#ConnectionProxyImpl
```java
public ConnectionProxyImpl(DataSourceProxy dataSource, Connection connection, Properties properties, long id){
        // 将物理连接对象ConnectionImpl传给ConnectionProxyImpl的connection属性
        super(connection, id);
        this.dataSource = dataSource;
        this.connection = connection;
        this.properties = properties;
        this.connectedTime = System.currentTimeMillis();
    }
```

```java
public abstract class WrapperProxyImpl implements WrapperProxy {

    private final Wrapper raw;

    private final long id;

    private Map<String, Object> attributes; // 不需要线程安全
    // 父类WrapperProxyImpl的构造方法
    // 将物理连接对象ConnectionImpl传给WrapperProxyImpl的ram属性,将connectionId传给id属性
    public WrapperProxyImpl(Wrapper wrapper, long id) {
        this.raw = wrapper;
        this.id = id;
    }
}
```

后面`getConnection`获取的连接对象就都是`ConnectionProxyImpl`类型对象。