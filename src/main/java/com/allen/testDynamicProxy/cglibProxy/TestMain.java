package com.allen.testDynamicProxy.cglibProxy;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.Enhancer;

/**
 * @Author: allen
 * @Date: 2022/3/27 3:35 PM
 * @Description:
 **/
public class TestMain {

    /**
     * 用于生成 Cglib 动态代理类工具方法
     * @param target 代表需要 被代理的 委托类的 Class 对象
     * @return 代理类对象
     */
    public static Object cglibProxyGenerator(Class target) {
        // 创建加强器，用来创建动态代理类
        Enhancer enhancer = new Enhancer();
        // 为代理类指定需要代理的类，也即是父类
        enhancer.setSuperclass(target);
        // 设置方法拦截器回调引用，对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept() 方法进行拦截
        enhancer.setCallback(new CglibMethodInterceptor());
        // 创建cglib 代理类
        return enhancer.create();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "classFiles");
        Dog poolDog = (Dog) cglibProxyGenerator(Dog.class);
        poolDog.call();
    }
}
