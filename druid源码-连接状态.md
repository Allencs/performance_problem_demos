## druid连接状态源码【随笔】

### disable设置情况：
1. com.alibaba.druid.pool.DruidPooledConnection#close 关闭连接时会设置【即执行完recycle方法之后】

2. recycle最终会调用com.alibaba.druid.pool.DruidDataSource#即执行完recycle方法
如果discard为true，直接返回

3. 在连接检测，com.alibaba.druid.pool.DruidAbstractDataSource#testConnectionInternal(com.alibaba.druid.pool.DruidConnectionHolder, java.sql.Connection)中，
如果最后数据包间隔时间大于timeBetweenEvictionRunsMillis，将holder discard属性设置true。

### 连接回收recycle()方法源码
```java
public void recycle() throws SQLException {
        // 如果DruidPooledConnection的disable为true，不回收
        if (this.disable) {
            return;
        }

        DruidConnectionHolder holder = this.holder;
        if (holder == null) {
            if (dupCloseLogEnable) {
                LOG.error("dup close");
            }
            return;
        }

        if (!this.abandoned) {
            DruidAbstractDataSource dataSource = holder.getDataSource();
            dataSource.recycle(this);
        }

        this.holder = null;
        conn = null;
        transactionInfo = null;
        closed = true;
    }
```

### removeAbandoned()方法源码：
```java
/** 
* 不清楚条件：
* 1、pooledConnection.isRunning() transaction未提交
* 2. pooledConnection.isDisable()，DruidPooledConnection的disable为true
*/
     public int removeAbandoned() {
        int removeCount = 0;

        long currrentNanos = System.nanoTime();

        List<DruidPooledConnection> abandonedList = new ArrayList<DruidPooledConnection>();

        activeConnectionLock.lock();
        try {
            Iterator<DruidPooledConnection> iter = activeConnections.keySet().iterator();

            for (; iter.hasNext();) {
                DruidPooledConnection pooledConnection = iter.next();
                // transaction未提交
                if (pooledConnection.isRunning()) {
                    continue;
                }

                long timeMillis = (currrentNanos - pooledConnection.getConnectedTimeNano()) / (1000 * 1000);

                if (timeMillis >= removeAbandonedTimeoutMillis) {
                    // 从activeConnections中移除
                    iter.remove();
                    pooledConnection.setTraceEnable(false);
                    abandonedList.add(pooledConnection);
                }
            }
        } finally {
            activeConnectionLock.unlock();
        }

        if (abandonedList.size() > 0) {
            for (DruidPooledConnection pooledConnection : abandonedList) {
                final ReentrantLock lock = pooledConnection.lock;
                lock.lock();
                try {
                    if (pooledConnection.isDisable()) {
                        continue;
                    }
                } finally {
                    lock.unlock();
                }

                JdbcUtils.close(pooledConnection);
                pooledConnection.abandond();
                removeAbandonedCount++;
                removeCount++;

                if (isLogAbandoned()) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("abandon connection, owner thread: ");
                    buf.append(pooledConnection.getOwnerThread().getName());
                    buf.append(", connected at : ");
                    buf.append(pooledConnection.getConnectedTimeMillis());
                    buf.append(", open stackTrace\n");

                    StackTraceElement[] trace = pooledConnection.getConnectStackTrace();
                    for (int i = 0; i < trace.length; i++) {
                        buf.append("\tat ");
                        buf.append(trace[i].toString());
                        buf.append("\n");
                    }

                    buf.append("ownerThread current state is " + pooledConnection.getOwnerThread().getState()
                               + ", current stackTrace\n");
                    trace = pooledConnection.getOwnerThread().getStackTrace();
                    for (int i = 0; i < trace.length; i++) {
                        buf.append("\tat ");
                        buf.append(trace[i].toString());
                        buf.append("\n");
                    }

                    LOG.error(buf.toString());
                }
            }
        }

        return removeCount;
    }
```

```java
protected boolean testConnectionInternal(DruidConnectionHolder holder, Connection conn) {
   String sqlFile = JdbcSqlStat.getContextSqlFile();
   String sqlName = JdbcSqlStat.getContextSqlName();

   if (sqlFile != null) {
       JdbcSqlStat.setContextSqlFile(null);
   }
   if (sqlName != null) {
       JdbcSqlStat.setContextSqlName(null);
   }
   try {
       if (validConnectionChecker != null) {
           boolean valid = validConnectionChecker.isValidConnection(conn, validationQuery, validationQueryTimeout);
           long currentTimeMillis = System.currentTimeMillis();
           if (holder != null) {
               holder.lastValidTimeMillis = currentTimeMillis;
               holder.lastExecTimeMillis = currentTimeMillis;
           }

           if (valid && isMySql) { // unexcepted branch
               long lastPacketReceivedTimeMs = MySqlUtils.getLastPacketReceivedTimeMs(conn);
               if (lastPacketReceivedTimeMs > 0) {
                   long mysqlIdleMillis = currentTimeMillis - lastPacketReceivedTimeMs;
                   
                   // 如果最后数据包间隔时间大于timeBetweenEvictionRunsMillis，将holder discard属性设置true
                   if (lastPacketReceivedTimeMs > 0 //
                           && mysqlIdleMillis >= timeBetweenEvictionRunsMillis) {
                       discardConnection(holder);
                       String errorMsg = "discard long time none received connection. "
                               + ", jdbcUrl : " + jdbcUrl
                               + ", version : " + VERSION.getVersionNumber()
                               + ", lastPacketReceivedIdleMillis : " + mysqlIdleMillis;
                       LOG.warn(errorMsg);
                       return false;
                   }
               }
                }
                // ······
    }
```