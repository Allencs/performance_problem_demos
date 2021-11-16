package com.allen.testLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestLog {

    private final static Logger logger = LoggerFactory.getLogger(TestLog.class);
    private final ThreadPoolExecutor executor;

    public TestLog() {
        this.executor = new ThreadPoolExecutor(10, 10,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public void testLog() {
        for (int i = 0; i < this.executor.getCorePoolSize(); i++) {
            this.executor.execute(() -> {
                while (true) {
                    logger.info("测试日志.... + " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new TestLog().testLog();
    }
}
