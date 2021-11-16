package com.allen.singleInstance;


/**
 *和饿汉模式一样，是靠JVM保证类的静态成员只能被加载一次的特点，这样就从JVM层面保证了只会有一个实例对象。那么问题来了，
 *这种方式和饿汉模式又有什么区别呢？不也是立即加载么？实则不然，加载一个类时，其内部类不会同时被加载。
 *一个内部类被加载，当且仅当其某个静态成员（静态域、构造器、静态方法等）被调用时发生。
 */
public class SingleInstanceInner {
    private SingleInstanceInner() {}

    private static class Inner {
        private static SingleInstanceInner singleInstanceInner = new SingleInstanceInner();
    }

    public static SingleInstanceInner getInstance() {
        return Inner.singleInstanceInner;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> System.out.println(SingleInstanceInner.getInstance().hashCode())).start();
        }
    }
}


