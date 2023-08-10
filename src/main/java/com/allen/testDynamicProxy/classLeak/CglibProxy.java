package com.allen.testDynamicProxy.classLeak;

import com.allen.testDynamicProxy.cglibProxy.Dog;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.Enhancer;
import java.lang.reflect.Method;

/**
 * @Author: allen
 * @Date: 2023/8/10 16:51
 * @Description: 动态代理不断加载类，导致元空间溢出(-XX:MaxMetaspaceSize=32M -XX:+HeapDumpOnOutOfMemoryError)
 **/
public class CglibProxy implements MethodInterceptor {

    /**
     * 创建代理对象
     */
    public Object getProxy(Class clazz) {
        Enhancer enhancer = new Enhancer();
        // 设置需要创建子类的类
        enhancer.setSuperclass(clazz);
        // 回调方法
        enhancer.setCallback(this);
        enhancer.setUseCache(false);
        // 通过字节码技术动态创建子类实例
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        long startTime = System.nanoTime(); // 获取开始时间
        Object result = methodProxy.invokeSuper(obj, args);   // 通过代码类调用父类中的方法
        long endTime = System.nanoTime();   // 获取结束时间
        System.out.println("运行时间： " + (endTime - startTime) + "ns");
        return result;
    }

    public static void main(String[] args) {
        CglibProxy proxy = new CglibProxy();
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 通过生成子类的方式建立代理类
            Dog proxyTmp = (Dog) proxy.getProxy(Dog.class);
            proxyTmp.call();
        }
    }
}
