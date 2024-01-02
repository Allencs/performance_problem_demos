package com.allen.testReflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestRef {

    static Class<?> clazz;

    static {
        try {
            clazz = Class.forName("com.allen.entities.CommonTestEntity");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void testGC() {

        try {
            Object refTest = clazz.newInstance();
            Method method = clazz.getMethod("testGC");
            for (int i = 0; i < 100; i++) {
                method.invoke(refTest);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        testGC();
        try {
            Class<?> clazz = Class.forName("com.allen.entities.CommonTestEntity");
            Object refTest = clazz.newInstance();
            Method method = clazz.getMethod("defaultMethod");
            clazz.getMethods();
            Method method1 = clazz.getDeclaredMethod("showAge");
            method.invoke(refTest);
            for (int i = 0; i < 100000; i++) {
                Thread.sleep(1000);
                method.invoke(refTest);
                // 每个方法都会创建一个GeneratedMethodAccessor
                method1.invoke(refTest);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
