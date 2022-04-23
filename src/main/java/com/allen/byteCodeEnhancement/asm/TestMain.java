package com.allen.byteCodeEnhancement.asm;

import com.allen.byteCodeEnhancement.Base;

import java.io.IOException;

/**
 * @Author: allen
 * @Date: 2022/4/21 18:33
 * @Description:
 **/
public class TestMain {

    public static void main(String[] args) throws IOException {
//        new ClassGenerator().generate();
        new Base().process();
    }
}
