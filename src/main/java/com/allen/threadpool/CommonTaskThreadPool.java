package com.allen.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: allen @Date: 2024/3/21 12:47 @Description: 通用任务线程池
 */
public class CommonTaskThreadPool {
  private static final int QUEUE_MAX_SIZE = 1500;

  public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR =
      new ThreadPoolExecutor(
          10,
          20,
          30L,
          TimeUnit.SECONDS,
          new LinkedBlockingQueue<>(QUEUE_MAX_SIZE),
          new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
              Thread t = new Thread(r);
              String threadName = "task-thread-" + count.addAndGet(1);
              t.setName(threadName);
              return t;
            }
          },
          new ThreadPoolExecutor.AbortPolicy());

  public static Future<?> submit(Callable<?> task) {
    return THREAD_POOL_EXECUTOR.submit(task);
  }

  public static void submit(Runnable runnable) {
    THREAD_POOL_EXECUTOR.submit(runnable);
  }
}
