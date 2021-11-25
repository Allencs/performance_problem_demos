# Log4j2源码记录

1. 等待策略（DisruptorUtil类）
```
static WaitStrategy createWaitStrategy(final String propertyName, final long timeoutMillis) {
        final String strategy = PropertiesUtil.getProperties().getStringProperty(propertyName, "TIMEOUT");
        LOGGER.trace("property {}={}", propertyName, strategy);
        final String strategyUp = strategy.toUpperCase(Locale.ROOT); // TODO Refactor into Strings.toRootUpperCase(String)
        switch (strategyUp) { // TODO Define a DisruptorWaitStrategy enum?
        case "SLEEP":
            return new SleepingWaitStrategy();
        case "YIELD":
            return new YieldingWaitStrategy();
        case "BLOCK":
            return new BlockingWaitStrategy();
        case "BUSYSPIN":
            return new BusySpinWaitStrategy();
        case "TIMEOUT":
            return new TimeoutBlockingWaitStrategy(timeoutMillis, TimeUnit.MILLISECONDS);
        default:
            return new TimeoutBlockingWaitStrategy(timeoutMillis, TimeUnit.MILLISECONDS);
        }
    }
```

2. 配置 `AsyncLogger.SynchronizeEnqueueWhenQueueFull=false AsyncLoggerConfig.SynchronizeEnqueueWhenQueueFull=false` 
主要影响代码（AsyncLoggerConfigDisruptor类）
```
private void enqueue(final LogEvent logEvent, final AsyncLoggerConfig asyncLoggerConfig) {
        if (synchronizeEnqueueWhenQueueFull()) {
            synchronized (queueFullEnqueueLock) {
                disruptor.getRingBuffer().publishEvent(translator, logEvent, asyncLoggerConfig);
            }
        } else {
            disruptor.getRingBuffer().publishEvent(translator, logEvent, asyncLoggerConfig);
        }
    }

    private boolean synchronizeEnqueueWhenQueueFull() {
        return DisruptorUtil.ASYNC_CONFIG_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL
                // Background thread must never block
                && backgroundThreadId != Thread.currentThread().getId();
    }
```