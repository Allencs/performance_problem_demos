package com.allen.testConnectionPool.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestGetConnect {

    private static Logger logger = LoggerFactory.getLogger(TestGetConnect.class);

    // 数据库连接
    private HikariDataSource dataSource;

    TestGetConnect() {

        HikariConfig config = new HikariConfig();
//        config.setMaximumPoolSize(20);
//        config.setDataSourceClassName("com.mysql.jdbc.Driver");
//        config.addDataSourceProperty("serverName", "10.200.1.32");
//        config.addDataSourceProperty("port", "3306");
//        config.addDataSourceProperty("databaseName", "bt_order");
//        config.addDataSourceProperty("user", "root");
//        config.addDataSourceProperty("password", "6SBzj9H@uB1q#");
//        dataSource = new HikariDataSource(config);

//        // 也可以这样写
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        config.setJdbcUrl("jdbc:mysql://192.168.3.20:3306/hello_mybatis?useUnicode=true&characterEncoding=utf8&useSSL=false");
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/master_vehicle?useUnicode=true&characterEncoding=utf8&useSSL=false");
        config.setUsername("root");
        config.setPassword("123456");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(90000);
        dataSource = new HikariDataSource(config);
    }


    public Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        /**
         * 测试由于慢SQL导致的性能问题表现。反映到线程栈，从连结池获取连接的getConnection方法耗时会很严重
         */
        TestGetConnect testGetConnect = new TestGetConnect();
        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        int i = 0;
        while (i < 1000) {
            executorService.submit(() -> {
                try {
                    long starTime = System.currentTimeMillis();
                    Connection conn = testGetConnect.getConnection();
                    // 模拟SQL查询耗时
                    Thread.sleep(1000);
                    conn.close();
                    logger.info("线程：「{}」查询结束，耗时：{}", Thread.currentThread().getName(), (System.currentTimeMillis() - starTime));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
//            Thread.sleep(1000);
            i++;
        }
        executorService.shutdown();
    }
}
