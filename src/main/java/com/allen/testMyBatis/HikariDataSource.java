package com.allen.testMyBatis;

import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;

public class HikariDataSource extends PooledDataSourceFactory {

    public HikariDataSource() {
        this.dataSource = new com.zaxxer.hikari.HikariDataSource();
    }

}
