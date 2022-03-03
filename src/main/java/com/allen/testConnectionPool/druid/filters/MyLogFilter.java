package com.allen.testConnectionPool.druid.filters;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @Author: allen
 * @Date: 2022/3/3 4:14 下午
 * @Description:
 */
public class MyLogFilter extends FilterEventAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MyLogFilter.class);
    private static long timeMessage;
    @Override
    public void init(DataSourceProxy dataSource) {
        logger.info("初始化Filter");
        super.init(dataSource);
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        logger.info("自定义拦截，在执行操作前执行该方法，如打印执行sql："+sql);
        timeMessage = System.currentTimeMillis();
        super.statementExecuteBefore(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        super.statementExecuteAfter(statement, sql, result);
        logger.info("自定义拦截器，在执行操作后执行该方法，如打印执行sql：  "+ sql);
        if (System.currentTimeMillis() - timeMessage > 0){
            logger.error("Error Sql : " + sql);
        }
    }

    public static List<Filter> getFilter() {
        return Lists.newArrayList(new MyLogFilter());
    }
}
