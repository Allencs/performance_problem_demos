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

# Disruptor源码

1. Disruptor在初始化的时候创建指定大小的RingBuffer，RingBuffer初始化的时候会将保存核心数据的数组填充满【数据内容为自定义的Event对象】；
```aidl
abstract class RingBufferFields<E> extends RingBufferPad
{
    private static final int BUFFER_PAD;
    private static final long REF_ARRAY_BASE;
    private static final int REF_ELEMENT_SHIFT;
    private static final Unsafe UNSAFE = Util.getUnsafe();

    static
    {
        final int scale = UNSAFE.arrayIndexScale(Object[].class);
        if (4 == scale)
        {
            REF_ELEMENT_SHIFT = 2;
        }
        else if (8 == scale)
        {
            REF_ELEMENT_SHIFT = 3;
        }
        else
        {
            throw new IllegalStateException("Unknown pointer size");
        }
        BUFFER_PAD = 128 / scale;
        // Including the buffer pad in the array base offset
        REF_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class) + (BUFFER_PAD << REF_ELEMENT_SHIFT);
    }

    private final long indexMask;
    private final Object[] entries;
    protected final int bufferSize;
    protected final Sequencer sequencer;

    RingBufferFields(
        EventFactory<E> eventFactory,
        Sequencer sequencer)
    {
        this.sequencer = sequencer;
        this.bufferSize = sequencer.getBufferSize();

        if (bufferSize < 1)
        {
            throw new IllegalArgumentException("bufferSize must not be less than 1");
        }
        if (Integer.bitCount(bufferSize) != 1)
        {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }

        this.indexMask = bufferSize - 1;
        this.entries = new Object[sequencer.getBufferSize() + 2 * BUFFER_PAD];
        // 预分配对象
        fill(eventFactory);
    }

    private void fill(EventFactory<E> eventFactory)
    {
        for (int i = 0; i < bufferSize; i++)
        {
            entries[BUFFER_PAD + i] = eventFactory.newInstance();
        }
    }

    @SuppressWarnings("unchecked")
    protected final E elementAt(long sequence)
    {
        return (E) UNSAFE.getObject(entries, REF_ARRAY_BASE + ((sequence & indexMask) << REF_ELEMENT_SHIFT));
    }
}
```
2. RingBuffer可以循环写的核心思想是，使用sequence的下标和bufferSize进行取模运算（位运算）；数据的覆盖其实是自定义Event对象属性的覆盖；

3. Disruptor主要通过Sequence操作可读/可写区间；Sequence在处理的时候在左右分别设置7位padding long【缓存填充数据】保证独占缓存行解决伪共享问题；

```aidl
class LhsPadding
{
    protected long p1, p2, p3, p4, p5, p6, p7;
}

class Value extends LhsPadding
{
    protected volatile long value;
}

class RhsPadding extends Value
{
    protected long p9, p10, p11, p12, p13, p14, p15;
}
```
同样的设计思想存在于RingBuffer：
```aidl
abstract class RingBufferPad
{
    protected long p1, p2, p3, p4, p5, p6, p7;
}

abstract class RingBufferFields<E> extends RingBufferPad
{
    private static final int BUFFER_PAD;
    private static final long REF_ARRAY_BASE;
    private static final int REF_ELEMENT_SHIFT;
    private static final Unsafe UNSAFE = Util.getUnsafe();
    ....
}

public final class RingBuffer<E> extends RingBufferFields<E> implements Cursored, EventSequencer<E>, EventSink<E>
{
    public static final long INITIAL_CURSOR_VALUE = Sequence.INITIAL_VALUE;
    protected long p1, p2, p3, p4, p5, p6, p7;
    ....
}
```

// 无锁