package com.allen.testDynamicProxy.jdkProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 每个代理实例都需要关联一个invocation handler，当一个方法被代理实例调用时，这个方法会被编码并发送到invocation handler中进行处理
 */
public class JdkInvocationHandler implements InvocationHandler {

    private ProductService target;

    public Object getInstance(ProductService target){
        this.target = target;
        Class clazz = this.target.getClass();
        // 参数1：被代理类的类加载器 参数2:被代理类的接口 参数3:Invocation本身
        // 实例化proxy的操作也可以移到外部实现
        return Proxy.newProxyInstance(clazz.getClassLoader(),
                clazz.getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object res = method.invoke(this.target, args);
        System.out.println("添加产品耗时：" + (System.currentTimeMillis() - startTime) + "s");
        return res;
    }
}
