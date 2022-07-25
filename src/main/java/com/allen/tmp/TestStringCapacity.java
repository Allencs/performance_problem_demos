package com.allen.tmp;

/**
 * @Author: allen
 * @Date: 2022/7/19 10:36
 * @Description:
 */
public class TestStringCapacity {

    private final static int actualLen = 134217728;

    private final static int biggerLen = 161619968;

    public static void main(String[] args) {

//        for (int i = 0; i < 3; i++) {
//            new Thread(() -> {
//                StringBuilder sb = new StringBuilder();
//                int num=1;
//                try {
//                    for ( ; num<Integer.MAX_VALUE; num++ )
//                        sb.append("x");
//                } catch ( OutOfMemoryError e ) {
//                    System.out.println(Thread.currentThread().getName() + " | " +  num);
//                    System.out.println(Integer.MAX_VALUE);
//                    e.printStackTrace();
//                }
//            }).start();
//        }

        StringBuilder sb = new StringBuilder();
        int num=1;
        try {
            for ( ; num<Integer.MAX_VALUE; num++ )
                sb.append("x");
        } catch ( OutOfMemoryError e ) {
            System.out.println(num);
            System.out.println(Integer.MAX_VALUE);
            e.printStackTrace();
        }

        /**
         * -Xmx1g
         * eden:262144K【可分配字符数组长度134217728】
         */
//        char[] values = new char[biggerLen];
//        int num = 0;
//        try {
//            for ( ; num < values.length; num++ )
//                values[num] = "x".charAt(0);
//
//            System.out.println("数组装配完成，开始拷贝");
//            char[] copy = new char[biggerLen << 1];
//            System.arraycopy(values, 0, copy, 0,
//                    biggerLen);
//            System.out.println("数组拷贝完成");
//        } catch (Exception e) {
//            System.out.println(num);
//            System.out.println(e.getMessage());
//        }
    }
}
