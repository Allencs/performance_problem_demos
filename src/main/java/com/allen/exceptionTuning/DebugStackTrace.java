package com.allen.exceptionTuning;

/**
 * @Author: allen
 * @Date: 2022/6/21 12:29 PM
 * @Description: javaClasses.cpp、Throwable.c
 */
public class DebugStackTrace {

    public static void main(String[] args) throws Exception {
        Exception e = new Exception("出错了...");
        e.fillInStackTrace();
        throw e;
    }
}
