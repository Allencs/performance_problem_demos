package com.allen.byteCodeEnhancement.instrument.agentmain;

import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtField;
import org.apache.ibatis.javassist.CtMethod;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @Author: allen
 * @Date: 2022/4/23 14:13
 * @Description: instrument类修改demo
 **/
public class AgentmainTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("To transform class: " + className);
        byte[] byteClass = null;
        try {
            ClassPool cp = ClassPool.getDefault();
            CtClass cc = cp.get("com.allen.byteCodeEnhancement.Base");
            // 添加新字段$jacocoData（会导致attach报错）
//            CtField f1 = CtField.make("private static transient boolean[] $jacocoData;", cc);
//            f1.setModifiers(8);
//            cc.addField(f1);
            CtMethod m = cc.getDeclaredMethod("process");
            m.insertBefore("{ System.out.println(\"start...【enhance by javassist and instrument】\"); }");
            m.insertAfter("{ System.out.println(\"end【enhance by javassist and instrument】\"); }");
            cc.writeFile();
            byteClass =  cc.toBytecode();
            // If a CtClass object is converted into a class file by writeFile(), toClass(), or toBytecode(),
            // Javassist freezes that CtClass object. Further modifications of that CtClass object are not permitted.
            // This is for warning the developers when they attempt to modify a class file that has been already
            // loaded since the JVM does not allow reloading a class.
//            if (cc.isFrozen()) {
//                cc.defrost();
//            }
            return byteClass;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
