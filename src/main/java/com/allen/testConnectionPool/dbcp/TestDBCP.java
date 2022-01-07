package com.allen.testConnectionPool.dbcp;

import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TestDBCP {

    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/master_vehicle?useUnicode=true&characterEncoding=utf8&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        //初始连接数
        dataSource.setMaxTotal(30);
        //最大空闲数
        dataSource.setMaxIdle(10);
        //最小空闲数
        dataSource.setMinIdle(10);
        //最长等待时间(ms)
        dataSource.setMaxWaitMillis(60000);
        //指定时间内未使用连接则关闭(s)
//        dataSource.setRemoveAbandonedTimeout(100);
        //程序中的连接不使用后是否被连接池回收
//        dataSource.setRemoveAbandonedOnBorrow(true);
//        dataSource.setRemoveAbandonedOnMaintenance(true);
    }

    //从连接池中获取一个连接
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) throws SQLException, InterruptedException {
        for (int i = 0; i < 15; i++) {
            new Thread(() -> {
                Connection connection = TestDBCP.getConnection();
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = connection.prepareStatement("select * from master_vehicle.vehicle limit 5");
                    ResultSet resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    System.out.println(resultSet.getString("vin"));
                    Thread.sleep(120000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
