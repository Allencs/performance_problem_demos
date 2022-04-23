package com.allen.byteCodeEnhancement.instrument.premain;

import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * @Author: allen
 * @Date: 2022/4/23 14:13
 * @Description: instrument类修改demo
 **/
public class PremainTransformer implements ClassFileTransformer {

    private final ClassPool cp;

    public PremainTransformer(ClassPool cp) {
        this.cp = cp;
    }
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        System.out.println("premain load class: " + className);

        if ("com/allen/byteCodeEnhancement/Base".equals(className)) {
            System.out.println("start enhancing [Base]");
            try {
                CtClass cc = cp.get("com.allen.byteCodeEnhancement.Base");
                CtMethod m = cc.getDeclaredMethod("process");
                m.insertBefore("{ System.out.println(\"start...【enhance by javassist and instrument(premain)】\"); }");
                m.insertAfter("{ System.out.println(\"end【enhance by javassist and instrument(premain)】\"); }");
                return cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
