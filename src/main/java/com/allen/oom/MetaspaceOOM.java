package com.allen.oom;

import com.allen.classLoader.MyClassLoader;

import java.util.ArrayList;

public class MetaspaceOOM {

    private static final ArrayList<MyClassLoader> classloaders = new ArrayList<>();

    // -XX:MaxMetaspaceSize=32m 限制元空间使用上限
    // -XX:MaxMetaspaceSize=32m -XX:+PrintGC -XX:+PrintGCDetails -Xloggc:gc.log

    public static void main(String[] args) {
        for (int i = 0; i < 100000; i++) {
            try {
                MyClassLoader classLoader = new MyClassLoader("classFiles");
                // 将类加载器保存至静态集合中，避免垃圾回收
                classloaders.add(classLoader);
                // 加载类文件
                Class clazz = classLoader.loadClass("com.allen.Person");
                // 创建类的实例化对象
                Object object = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
