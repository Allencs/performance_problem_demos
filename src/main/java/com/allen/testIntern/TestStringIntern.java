package com.allen.testIntern;

/**
 * @Author: allen
 * @Date: 2022/7/5 10:49
 * @Description:
 **/
public class TestStringIntern {

    public static void main(String[] args) {
//        String s = new String("1");
//        s.intern();
//        String s2 = "1";
//        System.out.println(s == s2);
//
//        String s3 = new String("1") + new String("1");
//        s3.intern();
//        String s4 = "11";
//        System.out.println(s3 == s4);
//        String s5 = "dmz";
//        String s3 = new StringBuilder().append("d").append("mz").toString();
//        String s4 = s3.intern();
//        System.out.println(s5 == s4);

        String s7 = new StringBuilder().append("s").append("pring").toString();
        String s8 = s7.intern();
        String s6 = "spring";
        System.out.println(s8 == s7);
        System.out.println(s6 == s7);
    }
}
