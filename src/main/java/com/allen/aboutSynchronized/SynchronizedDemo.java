package com.allen.aboutSynchronized;

public class SynchronizedDemo {

    /**
     * 实例方法，锁的是该类的实例对象
     */
    public synchronized void method0() throws InterruptedException {
        Thread.sleep(60000);
        System.out.println("实例同步方法执行结束 - " + Thread.currentThread().getName());
    }

    /**
     * 静态方法，锁的是类对象
     * @throws InterruptedException
     */
    public static synchronized void method1() throws InterruptedException {
        Thread.sleep(60000);
        System.out.println("静态同步方法执行结束 - " + Thread.currentThread().getName());
    }

    /**
     * 同步代码块，锁的是该类的实例对象
     * @throws InterruptedException
     */
    public void method2() throws InterruptedException {
        System.out.println("准备执行同步块内容【锁实例对象】 - " + Thread.currentThread().getName());
        synchronized (this) {
            Thread.sleep(60000);
            System.out.println("实例同步块内容执行结束【锁实例对象】 - " + Thread.currentThread().getName());
        }
    }

    /**
     * 同步代码块，锁的是该类的类对象
     * @throws InterruptedException
     */
    public void method3() throws InterruptedException {
        System.out.println("准备执行同步块内容【锁类对象】 - " + Thread.currentThread().getName());
        synchronized (SynchronizedDemo.class) {
            Thread.sleep(60000);
            System.out.println("实例同步块内容执行结束【锁类对象】 - " + Thread.currentThread().getName());
        }
    }

    /**
     * 同步代码块，锁的是配置的实例对象
     * @throws InterruptedException
     */
    public void method4() throws InterruptedException {
        Object obj = new Object();
        System.out.println("准备执行同步块内容【锁任意实例对象】 - " + Thread.currentThread().getName());
        synchronized (obj) {
            Thread.sleep(60000);
            System.out.println("实例同步块内容执行结束【锁任意实例对象】 - " + Thread.currentThread().getName());
        }
    }

    /**
     * append中有锁的申请，但是sb变量是局部变量，没有逃逸出该方法
     * 无法被多个线程同时访问，可以进行锁消除
     * @param a
     * @param b
     * @return
     */
    public String eliminateLocksMethod(String a, String b) {
        StringBuffer sb = new StringBuffer();
        sb.append(a);
        sb.append(b);
        return sb.toString();
    }

    public static void main(String[] args) {
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                try {
                    synchronizedDemo.method0();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }
}
