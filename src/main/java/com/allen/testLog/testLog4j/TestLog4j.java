package com.allen.testLog.testLog4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class TestLog4j {

    private static Logger logger = LogManager.getLogger(TestLog4j.class);
    private final ThreadPoolExecutor executor;

    public TestLog4j() {
        this.executor = new ThreadPoolExecutor(50, 50,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public void testLog() {
        for (int i = 0; i < this.executor.getCorePoolSize(); i++) {
            this.executor.execute(() -> {
                while (true) {
                    logger.info("测试日志--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞" +
                            "--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞" +
                            "--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞--麻利麻利哄快阻塞");
                }
            });
        }
    }

    public static void main(String[] args) {
        new TestLog4j().testLog();
        LockSupport.parkNanos(1);
    }
}
