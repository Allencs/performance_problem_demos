package com.allen.testIntern;

import com.allen.utils.UuidUtil;

import java.util.ArrayList;

/**
 * @Author: allen
 * @Date: 2022/10/17 14:48
 * @Description: String.intern错误用法（大量非重复字符）
 */
public class InCorrectUse {

    // 防止垃圾回收
    private final static ArrayList<String> arrayList = new ArrayList<>(5000000);

    public static void main(String[] args) {

        for (int i = 0; i < 5000000; i++) {
            String uuid = UuidUtil.getStrUuid().intern();
            arrayList.add(uuid);
        }
        try {
            System.out.println("创建完毕");
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
