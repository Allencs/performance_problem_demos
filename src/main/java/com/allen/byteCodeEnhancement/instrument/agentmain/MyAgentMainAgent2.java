package com.allen.byteCodeEnhancement.instrument.agentmain;

import com.allen.byteCodeEnhancement.Base;

import java.lang.instrument.Instrumentation;

/**
 * @Author: allen
 * @Date: 2022/4/23 14:26
 * @Description: 借助Agent将instrument注入到正在运行的JVM中【动态字节码增强，Attach机制】
 **/
public class MyAgentMainAgent2 {

    public static void agentmain(String args, Instrumentation inst) {
        // 指定自己定义的Transformer，在其中利用Javassist做字节码替换
        inst.addTransformer(new AgentmainTransformer(), true);

        try {
            inst.retransformClasses(Base.class);
            System.out.println("Agent load successfully.");
        } catch (Exception e) {
            System.out.println("Agent load failed.");
            e.printStackTrace();
        }
    }
}
