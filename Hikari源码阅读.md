## Hikari源码阅读

底层基于ConcurrentBag存放连接对象；
ConcurrentBag两个主要的存储容器：
1. sharedList，CopyOnWriteArrayList类型变量，线程共享；
2. threadList，ThreadLocal<List<Object>>，线程私有；【保存sharedList中元素的引用】

### getConnection底层逻辑
- ConcurrentBag中通过borrow方法进行数据资源借用，通过requite方法进行资源回收，注意其中borrow方法只提供对象引用，不移除对象。
所以从bag中“借用”的items实际上并没有从任何集合中删除，因此即使引用废弃了，垃圾收集也不会发生。
&nbsp;
- ConcurrentBag中存放的PoolEntry（封装的连接对象）存在4种状态：
  1. int STATE_NOT_IN_USE = 0;
  2. int STATE_IN_USE = 1;
  3. int STATE_REMOVED = -1;
  4. int STATE_RESERVED = -2;

### 连接池耗尽判断规则
&emsp; 需要判断com.zaxxer.hikari.util.ConcurrentBag中sharedList的数组元素PoolEntry的state == 1的个数，如果等于maxPoolSize，
则连接池已耗尽。
