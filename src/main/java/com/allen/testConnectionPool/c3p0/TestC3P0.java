package com.allen.testConnectionPool.c3p0;

import com.allen.testConnectionPool.dbcp.TestDBCP;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestC3P0 {
    ComboPooledDataSource dataSource;

    public TestC3P0() {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass( "com.mysql.cj.jdbc.Driver" ); //loads the jdbc driver
            dataSource.setJdbcUrl( "jdbc:mysql://127.0.0.1:3306/master_vehicle?useUnicode=true&characterEncoding=utf8&useSSL=false" );
            dataSource.setUser("root");
            dataSource.setPassword("123456");
            dataSource.setMaxPoolSize(30);
            dataSource.setInitialPoolSize(10);
            dataSource.setMinPoolSize(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        TestC3P0 testC3P0 = new TestC3P0();
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                Connection connection = null;
                try {
                    connection = testC3P0.dataSource.getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
