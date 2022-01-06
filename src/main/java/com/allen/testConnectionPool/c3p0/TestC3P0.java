package com.allen.testConnectionPool.c3p0;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
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
        Connection connection = testC3P0.dataSource.getConnection();
        System.out.println(connection);
        try {
            Thread.sleep(180000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
