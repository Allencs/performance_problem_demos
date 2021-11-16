package com.allen.testConnectionPool.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

public class TestHikari {

    private final static Logger logger = LoggerFactory.getLogger(TestHikari.class);

    // 数据库连接
    private HikariDataSource dataSource;


    public TestHikari() {

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
        config.setDriverClassName("com.mysql.jdbc.Driver");
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


    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        TestHikari hPool = new TestHikari();
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                Connection conn = null;
                Statement statement = null;
                try {
                    conn = hPool.getConnection();
                    PreparedStatement preparedStatement = conn.prepareStatement("select *");
                    System.out.println(Thread.currentThread().getName() + ": 获取数据库连接成功。");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                try {
                    statement = conn.createStatement();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                ResultSet rs = null;
                try {
                    rs = statement.executeQuery("select * from scenario_info");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                while (true) {
                    try {
                        if (!rs.next()) break;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName() + "：结果集遍历完成。");
//                try {
//                    Thread.sleep(120000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                hPool.dataSource.close();
//                System.out.println(Thread.currentThread().getName() +  "：hPool.dataSource.close()");
                //关闭connection
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                try {
                    System.out.println(Thread.currentThread().getName() + "：开始睡眠。");
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }


    }
}
