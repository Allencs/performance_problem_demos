package com.allen.threads;


import org.springframework.core.task.SimpleAsyncTaskExecutor;

public class AboutThreadLocal {

    private final static ThreadLocal<String> myThreadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        myThreadLocal.set("A thread local value");
        System.out.println(myThreadLocal.get());

//        new Thread(() -> System.out.println(Thread.currentThread().getName() + ": " + myThreadLocal.get())).start();

        new Thread(() -> System.out.println(Thread.currentThread().getContextClassLoader())).start();

        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

    }
}
