## 包模块功能概览

### aboutBuffer
测试Buffer缓冲区对于http请求大报文体的性能提升。（暂无提升，区别于磁盘文件读写）

### aboutSynchronized
synchronized的作用域，synchronized相关优化（jvm字节码层面 -> 锁消除）

### beanCopy
CGLIB BeanCopier使用demo（apache BeanUtils性能比较差，推荐使用CGLIB提供的BeanCopier；）

### byteCodeEnhancement
字节码增强探究（包含javassist和asm简单使用）
1. premain
2. attach

### cglibproxy
CGLIB的简单使用

### classLoader
自定义类加载器

### commons
#### 1. entity
CommonTestEntity类 -> 用于单例测试
Person类 -> 公共类

#### 2. utils
UuidUtil -> uuid生成器

### cpuCacheLine
disruptor框架学习思考之 伪共享（cpu缓存行失效）

### disruptorDemo
Disruptor的简单使用demo

### exceptionTuning
自定义的异常不收集线程栈信息

### groovyComponents
java调用groovy SimpleTemplateEngine性能优化demo

### localCache
java本地缓存（Guava）

### memoryLimitedLBQ
内存安全的队列（借助Instrumentation实现）

### memorySafeLBQ
内存安全的队列（定时任务线程）

### oom
OOM demo

### perfTuning
JMH微基准测试demo

### queue
java队列demo【ArrayBlockingQueue】

### singleInstance
单例设计模式（使用静态内部类实现）

### springAop
Spring Aop简单demo

### springIoc
Spring IOC简单demo

### testCompletableFuture
CompletableFuture简单demo

### testCompressFile
1. 测试zip文件压缩
2. I/O优化
   > NIO Channel、InputStream和BufferInputStream、ByteBuffer.allocateDirect

### testConnectionPool
1. druid
2. hikari
3. c3p0
4. dbcp

### testDynamicProxy
1. CGLIB
2. JDK动态代理

### testHttpComponents
1. Apache HttpClient4 demo
2. OkHttp3 demo

### testKafka
kafka demo

### testLog
1. logback
2. log4j2

### testRedis
1. jackson序列化
2. redisson
   1. cluster
   2. single
3. jedis
   1. jedis cluster
   2. jedis
   3. jedisPool
4. lettuce

### testReflect
java反射探究

### testSPI
spi简单使用

### threads
1. org.springframework.core.task.SimpleAsyncTaskExecutor
2. 线程切换 -> 高CPU


