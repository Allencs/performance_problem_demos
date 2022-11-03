package com.allen.testIntern;

/**
 * @Author: allen
 * @Date: 2022/7/5 10:49
 * @Description:
 * -XX:+PrintStringTableStatistics输出字符串常量池统计信息
 * 使用-XX:StringTableSize可以设置StringTableSize，默认是60013，可以设置的最小值为 1009
 *
 * 1、使用 new 创建时，检测常量池中是否存在对应String对象,如果不存在则在字符串常量池中创建一个对象，最后在堆中创建字符串对象。
 * 2、如果直接使用字面量，则先看常量池中是否有相等的字符串，如果有，则返回此字符串的引用，如果没有，则在常量池中创建
 *
 * 调用intern()方法时，首先在常量池中查看是否已有相同的字符串（字符串是否相同使用String的equal方法判断），
 * 如果常量池中已有，则直接返回该字符串的地址。如果字符串常量池中不存在，就在常量池中创建一个指向该对象堆中实例的引用，并返回这个引用地址。
 **/
public class TestStringIntern {

    public static void main(String[] args) {
        // s7 堆中spring字符串对象地址，同时在常量池中创建spring对象
        String s6 = new String("spring");
        // s8 返回常量池中spring地址
        String s7 = s6.intern();
        // s6 返回常量池中spring地址
        String s8 = "spring";
        System.out.println(s6 == s7);
        System.out.println(s6 == s8);
        System.out.println(s8 == s7);
    }
}
