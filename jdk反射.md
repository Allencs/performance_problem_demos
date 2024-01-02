## JDK反射相关笔记

### GeneratedMethodAccessor引用链（垃圾回收）

![img.png](materials/jdk-reflection/GeneratedMethodAccessor垃圾回收.png)

测试代码如下：

![img.png](materials/jdk-reflection/测试代码.png)
testGC方法执行完后，method对象变为垃圾对象，