package com.allen.byteCodeEnhancement.instrument;

import com.allen.byteCodeEnhancement.Base;

import java.lang.management.ManagementFactory;

/**
 * @Author: allen
 * @Date: 2022/4/23 14:27
 * @Description:
 **/
public class TestMain {

    public static void main(String[] args) {
        Base base = new Base();
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String s = name.split("@")[0];
        // 打印当前Pid
        System.out.println("pid: " + s);
        while (true) {
            try {
                Thread.sleep(3000L);
            } catch (Exception e) {
                break;
            }
            base.process();
        }
    }
}
