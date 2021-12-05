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

---
> 因为一个文件读写调优，又学了一遍Linux zero-copy和java中的各种Buffer
## 关于java的DirectBuffer
参考知乎问答 [Java NIO中，关于DirectBuffer，HeapBuffer的疑问？](https://www.zhihu.com/question/57374068/answer/153398427)
`DirectBuffer`本身这个对象是在堆中，但是引用了一块非堆的native memory，这块内存实际上还是属于java进程的内存中，这个角度说的话，
`DirectBuffer`是在用户内存中。
- `DirectBuffer`的好处是减少了一次从jvm heap到native memory的copy操作（究其原因：堆内存在GC，对象地址可能会移动；除了CMS标记整理， 
其他垃圾收集算法都会做复制移动整理）；
- 这里还存在一个问题，如果设计操作系统底层的操作，**native memory的数据是否要copy到kernel buffer？？**。「个人感觉应该是这样」
> 可以从hotspot找到验证，其中会将C的buf拷贝到java的数组jbyteArray【写的时候也是需要先将java数组copy到C数组】：
```aidl
// jdk/src/share/native/java/io/FileInputStream.c
JNIEXPORT jint JNICALL
Java_java_io_FileInputStream_readBytes(JNIEnv *env, jobject this,
        jbyteArray bytes, jint off, jint len) {
    return readBytes(env, this, bytes, off, len, fis_fd);
}
// jdk/src/share/native/java/io/io_util.c
/*
 * The maximum size of a stack-allocated buffer.
 * 栈上能分配的最大buffer大小
 */
#define BUF_SIZE 8192
jint
readBytes(JNIEnv *env, jobject this, jbyteArray bytes,
          jint off, jint len, jfieldID fid)
{
    jint nread;
    char stackBuf[BUF_SIZE]; // BUF_SIZE=8192
    char *buf = NULL;
    FD fd;
    // 传入的Java byte数组不能是null
    if (IS_NULL(bytes)) {
        JNU_ThrowNullPointerException(env, NULL);
        return -1;
    }
    // off，len参数是否越界判断
    if (outOfBounds(env, off, len, bytes)) {
        JNU_ThrowByName(env, "java/lang/IndexOutOfBoundsException", NULL);
        return -1;
    }
    // 如果要读取的长度是0，直接返回读取长度0
    if (len == 0) {
        return 0;
    } else if (len > BUF_SIZE) {
        // 如果要读取的长度大于BUF_SIZE，则不能在栈上分配空间了，需要在堆上分配空间
        buf = malloc(len);
        if (buf == NULL) {
            // malloc分配失败，抛出OOM异常
            JNU_ThrowOutOfMemoryError(env, NULL);
            return 0;
        }
    } else {
        buf = stackBuf;
    }
    // 获取记录在FileDescriptor中的文件描述符
    fd = GET_FD(this, fid);
    if (fd == -1) {
        JNU_ThrowIOException(env, "Stream Closed");
        nread = -1;
    } else {
        // 调用IO_Read读取
        nread = IO_Read(fd, buf, len);
        if (nread > 0) {
            // 读取成功后，从buf拷贝数据到Java的byte数组中
            (*env)->SetByteArrayRegion(env, bytes, off, nread, (jbyte *)buf);
        } else if (nread == -1) {
            // read系统调用返回-1是读取失败
            JNU_ThrowIOExceptionWithLastError(env, "Read error");
        } else { /* EOF */
            // 操作系统read读取返回0认为是读取结束，Java中返回-1认为是读取结束
            nread = -1;
        }
    }
    // 如果使用的是堆空间（len > BUF_SIZE），需要手动释放
    if (buf != stackBuf) {
        free(buf);
    }
    return nread;
}
```
### java中针对这些copy操作的优化
#### 1. `DirectBuffer`「上面已经提到」

#### 2. `MappedByteBuffer`
映射出一个`DirectByteBuffer`，使用的是native memory「用户缓冲区」，然后利用底层的mmap技术（内存映射（memory map）），将java进程中这块native memory
映射到内核缓冲区中的文件所在地址。 这里其实减少了一次read()的系统调用，即少一次用户态到内核态的切换。「两次系统调用：1、mmap 2、write」
- 原本的一次本地文件读写操作流程是：【 `disk -> kernel buffer -> user buffer[native memory如果是直接内存就没有copy到heap的操作，native memory其实可以理解成c语言的heap，因此所有的native方法的调用都会涉及native memory] -> jvm heap`，写入操作就反过来】
- 使用mmap后的读写流程：【`disk -> kernel buffer -> disk`】，如果是将磁盘文件发送到网络，流程是这样的：【`disk -> kernel buffer -> socket buffer -> network interface`】

使用demo待补充

#### 3. NIO Channel `transferTo` & `transferFrom`
利用底层sendfile()技术，即发起一次系统调用，在内核态完成所有的数据传递。
发送磁盘文件到网络：`disk -> kernel buffer -> socket buffer -> network interface`

#### 3. NIO Channel `Pipe`



