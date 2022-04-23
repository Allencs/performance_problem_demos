package com.allen.byteCodeEnhancement.javassist;

import com.allen.byteCodeEnhancement.Base;
import org.apache.ibatis.javassist.*;

import java.io.IOException;

/**
 * @Author: allen
 * @Date: 2022/4/21 18:22
 * @Description: javassist实现aop案例
 **/
public class JavassistDemo {

    /**
     * Javassist关键类说明：
     * CtClass（compile-time class）：编译时类信息，它是一个Class文件在代码中的抽象表现形式，可以通过一个类的全限定名来获取一个CtClass对象，用来表示这个类文件。
     * ClassPool：ClassPool是保存CtClass信息的HashTable，Key为类名，Value为类名对应的CtClass对象。当我们需要对某个类进行修改时，就是通过pool.getCtClass("className")方法从pool中获取到相应的CtClass。
     * CtMethod、CtField：对应的是类中的方法和属性。
     */
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, InstantiationException, IllegalAccessException {
        ClassPool cp = ClassPool.getDefault();
        // 目标类
        CtClass ctClass = cp.get("com.allen.byteCodeEnhancement.Base");
        // 目标增强方法
        CtMethod ctMethod = ctClass.getDeclaredMethod("process");
        // 插入前置增强逻辑
        ctMethod.insertBefore("{System.out.println(\"start...【enhance by javassist】\");}");
        // 插入后置增强逻辑
        ctMethod.insertAfter("{System.out.println(\"end【enhance by javassist】\");}");
        Class<?> c = ctClass.toClass();
        // 将修改后的class文件输出到工程根目录
        ctClass.writeFile("./");
        Base newBase = (Base) c.newInstance();
        newBase.process();
    }
}
