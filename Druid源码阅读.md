## Druid 连接检测导致的频繁类加载

### `testConnectionInternal`连接测试的核心方法
在`com.alibaba.druid.pool.DruidAbstractDataSource.testConnectionInternal`中，会调用`getLastPacketReceivedTimeMs`方法，在
`getLastPacketReceivedTimeMs`方法中，就会进行类加载操作；


### `getLastPacketReceivedTimeMs`方法
获取连接离最后一次接收数据包的间隔时间。如果使用`mysql-connector-java 6`以上，底层调用的是`com.mysql.cj.jdbc.JdbcConnection`的`getIdleFor`方法，
即连接的空闲时长。

```java
/**
 * 
 * @param conn ：如果配置了自定义Filter，传入的conn就是ConnectionProxyImpl类型，否则就是ConnectionImpl类型
 */
public static long getLastPacketReceivedTimeMs(Connection conn) throws SQLException {
        // 现阶段知道如果配置了自定义Filter，这边class_connectionImpl就是null
        // 判断条件成立后，就会进行类加载，而Druid <= 1.1.22的版本是写死mysql-connector-java 5的，因此使用6+版本的驱动，会存在ClassNotFound的问题。
        // 而在一般的Spring框架下，会使用TomcatEmbeddedWebappClassLoader类加载器进行加载，首先会查找缓存，但是缓存里永远都没有，因此会不断的进行类加载操作。
        // 类加载的报错在loadClass方法内部catch之后未做任何操作
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

        if (class_connectionImpl == null) {
            return -1;
        }

        if(mysqlJdbcVersion6){
            if (classJdbc == null) {
                // 加载mysql连接扩展类
                classJdbc = Utils.loadClass("com.mysql.cj.jdbc.JdbcConnection");
            }

            if (classJdbc == null) {
                return -1;
            }

            if (getIdleFor == null && !getIdleForError) {
                try {
                    // 通过反射获取getIdleFor方法。实际上调用的是ConnectionImpl对象的getIdleFor方法。
                    getIdleFor = classJdbc.getMethod("getIdleFor");
                    getIdleFor.setAccessible(true);
                } catch (Throwable error) {
                    getIdleForError = true;
                }
            }

            if (getIdleFor == null) {
                return -1;
            }

            try {
                Object connImpl = conn.unwrap(class_connectionImpl);
                if (connImpl == null) {
                    return -1;
                }

                return System.currentTimeMillis()
                        - ((Long)
                            getIdleFor.invoke(connImpl))
                        .longValue();
            } catch (Exception e) {
                throw new SQLException("getIdleFor error", e);
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