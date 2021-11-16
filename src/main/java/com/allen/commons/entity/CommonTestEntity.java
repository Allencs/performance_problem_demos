package com.allen.commons.entity;

public class CommonTestEntity {

    static {
        System.out.println("CommonTestEntity执行类加载...");
    }

    public CommonTestEntity() {
        System.out.println(this.getClass() + " ｜ CommonTestEntity实例初始化 | " + this.getClass().getClassLoader());
    }

    public void defaultMethod() {
        System.out.println("执行实例方法：defaultMethod");
    }
}
