package com.allen.byteCodeEnhancement.asm;

import com.allen.byteCodeEnhancement.Base;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: allen
 * @Date: 2022/4/21 16:40
 * @Description: 字节码观察器
 * ClassVisitor是访问者模式的抽象接口，接口中定义了访问class各个部分的方法，比如访问方法，访问注解等。
 **/
public class MyClassVisitor extends ClassVisitor implements Opcodes {

    public MyClassVisitor(ClassVisitor classVisitor) {
        super(ASM9, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature,
                exceptions);
        // Base类中有两个方法：无参构造以及process方法，不增强构造方法
        if (!name.equals("<init>") && mv != null) {
            mv = new MyMethodVisitor(mv);
        }
        return mv;
    }

    class MyMethodVisitor extends MethodVisitor implements Opcodes {
        public MyMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM9, mv);
        }

        /**
         * visitCode方法，它会在ASM开始访问某一个方法的Code区【字节码文件 - 方法表 - 方法属性】时被调用，重写visitCode方法，添加前置自定义代码逻辑
         * “Code区”：源代码对应的JVM指令操作码，在进行字节码增强时重点操作的就是“Code区”这一部分。
         * “Code区”所在位置：字节码文件 - 方法表 - 方法的属性【javap -verbose反编译内容中的Code模块】
         */
        @Override
        public void visitCode() {
            super.visitCode();
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("start...【enhance by ASM】");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        /**
         * 判断了当前指令是否为无参数的“return”指令，如果是就在它的前面添加一些指令
         * @param opcode 字节码指令标识
         */
        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                    || opcode == Opcodes.ATHROW) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitLdcInsn("end【enhance by ASM】");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
            mv.visitInsn(opcode);
        }
    }

}
