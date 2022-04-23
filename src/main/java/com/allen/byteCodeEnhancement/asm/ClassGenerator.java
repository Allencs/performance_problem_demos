package com.allen.byteCodeEnhancement.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author: allen
 * @Date: 2022/4/21 16:39
 * @Description: ASM字节码读取和写入
 **/
public class ClassGenerator {

    public void generate() throws IOException {
        // 读取目标字节码文件
        ClassReader classReader = new ClassReader("com/allen/byteCodeEnhancement/Base");
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new MyClassVisitor(classWriter);
        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);
        byte[] data = classWriter.toByteArray();
        // 输出新的字节码文件
        File f = new File("target/classes/com/allen/byteCodeEnhancement/Base.class");
        FileOutputStream out = new FileOutputStream(f);
        out.write(data);
        out.close();

        System.out.println("finish generating new class file");
    }
}
