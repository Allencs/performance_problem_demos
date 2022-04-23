package com.allen.byteCodeEnhancement.instrument.premain;

import org.apache.ibatis.javassist.ClassPool;
import java.lang.instrument.Instrumentation;

/**
 * @Author: allen
 * @Date: 2022/4/23 16:50
 * @Description: 静态字节码增强，必须在目标进程main方法前启动
 * 将premain下的内容打包成jar，并添加至javaagent启动参数
 **/
public class MyPreMainAgent {

    public static void premain(String args, Instrumentation inst) {
        final ClassPool pool = ClassPool.getDefault();

        // 指定自己定义的Transformer，在其中利用Javassist做字节码替换
        inst.addTransformer(new PremainTransformer(pool));
        System.out.println("premain Agent load successfully.");
    }
}
