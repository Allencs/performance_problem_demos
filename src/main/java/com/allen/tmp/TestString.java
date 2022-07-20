package com.allen.tmp;

/**
 * @Author: allen
 * @Date: 2022/7/19 10:36
 * @Description:
 */
public class TestString {

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        int num=1;
        try {
            for ( ; num<Integer.MAX_VALUE; num++ )
                sb.append("x");
        } catch ( OutOfMemoryError e ) {
            System.out.println(num);
            System.out.println(Integer.MAX_VALUE);
        }
    }
}
