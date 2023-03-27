package com.allen.commons.entity;

public class CommonTestEntity {

    public String name;
    private Integer age;

    static {
        System.out.println("CommonTestEntity执行类加载...");
    }

    public CommonTestEntity() {
        System.out.println("ClassName: " + this.getClass().getName() + " ｜ CommonTestEntity实例初始化 | ClassLoader: " + this.getClass().getClassLoader());
    }

    public void defaultMethod() {
        System.out.println("执行实例方法：defaultMethod");
    }
}
