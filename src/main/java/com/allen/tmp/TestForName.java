package com.allen.tmp;

import com.allen.commons.entity.Person;

/**
 * @Author: allen
 * @Date: 2022/7/6 6:01 PM
 * @Description:
 */
public class TestForName {

    public static void main(String[] args) {

        try {
            Class clazz = Class.forName("com.allen.commons.entity.Person", true, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
