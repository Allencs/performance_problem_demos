package com.allen.testReflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestRef {

    public static void main(String[] args) {
        try {
            Class<?> clazz = Class.forName("com.allen.commons.entity.CommonTestEntity");
            Object refTest = clazz.newInstance();
            Method method = clazz.getMethod("defaultMethod");
            clazz.getMethods();
            Method method1 = clazz.getDeclaredMethod("defaultMethod");
//            method.invoke(refTest);
            for (int i = 0; i < 100000; i++) {
                Thread.sleep(500);
                method.invoke(refTest);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InterruptedException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
