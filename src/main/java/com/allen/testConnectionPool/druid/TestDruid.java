package com.allen.testConnectionPool.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.allen.testConnectionPool.druid.filters.MyLogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDruid {

    private final static Logger log = LoggerFactory.getLogger(TestDruid.class);

    private final static DruidDataSource dataSource;

    static {
        //数据源配置
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/master_vehicle");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver"); //这个可以缺省的，会根据url自动识别
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setProxyFilters(MyLogFilter.getFilter()); // 添加自定义过滤器

        //下面都是可选的配置
        dataSource.setInitialSize(10);  //初始连接数，默认0
        dataSource.setMaxActive(30);  //最大连接数，默认8
        dataSource.setMinIdle(10);  //最小闲置数
        dataSource.setMaxWait(2000);  //获取连接的最大等待时间，单位毫秒（设置后使用公平锁）
        dataSource.setPoolPreparedStatements(true); //缓存PreparedStatement，默认false
        dataSource.setMaxOpenPreparedStatements(20); //缓存PreparedStatement的最大数量，默认-1（不缓存）。大于0时会自动开启缓存PreparedStatement，所以可以省略上一句代码
        dataSource.setUseUnfairLock(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(true);
    }

    public static void main(String[] args) throws SQLException, InterruptedException {
        //获取连接
        Connection connection = dataSource.getConnection();

        //PreparedStatement接口
        String sql2 = "select * from vehicle where id = 1";
        PreparedStatement preparedStatement = connection.prepareStatement(sql2);
        preparedStatement.execute();

        new Thread(() -> {
            // 输出是否使用"非公平锁"
            log.info(Thread.currentThread().getName() + " | UseUnfairLock: " + dataSource.isUseUnfairLock());
        }).start();

        //获取连接
        Connection connection2 = dataSource.getConnection();
//        Thread.sleep(1200000);

        //关闭连接
        connection.close();
    }
}
