package com.allen.classLoader;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

public class MyClassLoader extends ClassLoader {

    private String classPath;
    private final static Logger logger = LoggerFactory.getLogger(MyClassLoader.class);
    public MyClassLoader(String classPath) {
        this.classPath = classPath;
    }

    private byte[] loadByte(String name) throws Exception {
        name = name.replaceAll("\\.", "/");
        FileInputStream fis = new FileInputStream(classPath + "/" + name
                + ".class");
        int len = fis.available();
        byte[] data = new byte[len];
        fis.read(data);
        fis.close();
        return data;

    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] data = loadByte(name);
            return defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }

    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);

            if (c == null) {
                // If still not found, then invoke findClass in order
                // to find the class.
                long t1 = System.nanoTime();

                //非自定义的类还是走双亲委派加载
                if (!name.startsWith("com.allen")){
                    c = this.getParent().loadClass(name);
                }else{
                    c = findClass(name);
                }

                // this is the defining class loader; record the stats
                sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                sun.misc.PerfCounter.getFindClasses().increment();
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }


    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, InterruptedException {
        MyClassLoader myClassLoader = new MyClassLoader("/Users/chensheng/IdeaProjects/performance_problem_demos/classFiles");
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {

                Thread.currentThread().setContextClassLoader(myClassLoader); //线程上下文类加载器

                for (int j = 0; j < 100; j++) {
                    try {
                        Class clazz = Thread.currentThread().getContextClassLoader().loadClass("com.allen.Person");
                        Object object = clazz.newInstance();
                        logger.info(String.valueOf(clazz.getClassLoader()));
                        latch.countDown();
                        Thread.sleep(5000);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }




//        for (int j = 0; j < 1000000; j++) {
//            try {
//                Class clazz = myClassLoader.loadClass("com.allen.TestMyClassLoader");
//                Object object = clazz.newInstance();
//
//                System.out.println(clazz.getClassLoader());
////                latch.countDown();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            }
//        }

        System.out.println("创建结束。。。。");

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //        MyClassLoader myClassLoader = new MyClassLoader("G:\\myclass");
//        Class clazz1 = myClassLoader.loadClass("com.allen.TestMyClassLoader");
//        Object obj1 = clazz1.newInstance();
//        System.out.println(clazz1.getClassLoader());
    }
}
