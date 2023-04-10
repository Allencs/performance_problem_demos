package com.allen.tmp;

/**
 * @Author: allen
 * @Date: 2022/11/18 13:59
 * @Description:
 **/
public class Calculate {

    private static volatile int counter = 0;
    public static void add() {
        counter++;
    }
    public static void reduce() {
        counter--;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 20000; i++) {
                add();
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 20000; i++) {
                reduce();
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // counter=0吗？？
        System.out.println(counter);
    }
}
