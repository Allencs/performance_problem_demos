package com.allen.oom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OOMTest {

    private static Logger logger = LoggerFactory.getLogger(OOMTest.class);

    static List<Object> list = new ArrayList<>();

    public static void main(String[] args) {
//        heapOom();
        directOom();
    }

    public static byte[] getBytesFromObject(Serializable obj) throws Exception {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bo);
        oos.writeObject(obj);
        return bo.toByteArray();
    }

    private static void heapOom() {
        /**
         *  Test heap memory oom
         *  -Xmx128m -XX:+UseG1GC -XX:-DoEscapeAnalysis -XX:+HeapDumpOnOutOfMemoryError -XX:NativeMemoryTracking=detail
         */
        for(int i=0; i<10000000; i++){
            String d = new Date().toString();
            OOMBean oomBean = new OOMBean(d, i);
            list.add(oomBean);
//            logger.info("add bean:" + (i + 1));
        }
    }

    private static void directOom() {
        /**
         *  Test direct memory oom
         *  -XX:MaxDirectMemorySize
         */
        for(int i=0; i<10000000; i++){
            String d = new Date().toString();
            OOMBean oomBean = new OOMBean(d, i);
            byte[] bytes;
            try {
                bytes = getBytesFromObject(oomBean);
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(100 * 1024);
                // 将byteBuffer加入静态集合中，防止垃圾回收
                list.add(byteBuffer);
                byteBuffer.put(bytes);
//                logger.info("add bean:" + (i + 1));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
