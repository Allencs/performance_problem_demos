package com.allen.testThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: allen
 * @Date: 2022/10/8 14:32
 * @Description: waitNanos高消耗问题（当前线程已经成为leader了，只需要等待堆顶任务到达执行时间即可）？？
 * 参考：https://tobebetterjavaer.com/thread/ScheduledThreadPoolExecutor.html#delayedworkqueue
 */
public class TestScheduledThreadPool {
    private final static Logger logger = LoggerFactory.getLogger(TestScheduledThreadPool.class);
    private static final ScheduledExecutorService executor = new
            ScheduledThreadPoolExecutor(1, Executors.defaultThreadFactory());

    public static void main(String[] args) {
        // 新建一个固定延迟时间的计划任务
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logger.info("执行定时任务");
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
