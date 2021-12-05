package com.allen.testDynamicProxy.cglibProxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法拦截器,不用依赖被代理业务类的引用
 */
public class CglibMethodInterceptor implements MethodInterceptor {

    /**
     * 用于生成 Cglib 动态代理类工具方法
     * @param target 代表需要 被代理的 委托类的 Class 对象
     * @return
     */
    public Object CglibProxyGeneratory(Class target) {
        // 创建加强器，用来创建动态代理类
        Enhancer enhancer = new Enhancer();
        // 为代理类指定需要代理的类，也即是父类
        enhancer.setSuperclass(target);
        // 设置方法拦截器回调引用，对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept() 方法进行拦截
        enhancer.setCallback(this);
        // 创建cglib 代理类
        return enhancer.create();
    }


    /**
     *功能主要是在调用业务类方法之前 之后添加统计时间的方法逻辑.
     *intercept 因为具有 MethodProxy methodProxy 参数的原因,不再需要代理类的引用对象了,直接通过 methodProxy 对象访问被代理对象的方法(这种方式更快)。
     *当然 也可以通过反射机制，通过 method 引用实例    Object result = method.invoke(target, args); 形式反射调用被代理类方法，
     *target 实例代表被代理类对象引用, 初始化 CglibMethodInterceptor 时候被赋值 。但是Cglib不推荐使用这种方式
     * @param object 代表Cglib 生成的动态代理类 对象本身
     * @param method 代理类中被拦截的接口方法 Method 实例
     * @param args 接口方法参数
     * @param methodProxy 用于调用父类真正的业务类方法。可以直接调用被代理类接口方法
     * @return 被代理类方法执行返回值
     * @throws Throwable
     */
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("before");
        MonitorUtil.start();
        Object result = methodProxy.invokeSuper(object, args); //Object result = method.invoke(target, args);
        System.out.println("after");
        MonitorUtil.finish(method.getName());
        return result;
    }
}
