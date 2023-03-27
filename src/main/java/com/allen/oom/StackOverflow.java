package com.allen.oom;

public class StackOverflow {

    private static int deep = 1;

    /**
     * 通过无限递归来模拟栈溢出
     */
    private static void recursion() {
        deep++;
        recursion();
    }

    public static void main(String[] args) {
        try {
            recursion();
        } catch (Throwable e) { // catch 捕获的是 Throwable，而不是 Exception。因为 StackOverflowError 不属于 Exception 的子类。
            System.out.println("Stack deep : " + deep);
            e.printStackTrace();
        }

        // 不让进程结束，便于使用分析工具来查看内存情况
        try {
            Thread.sleep(24 * 60 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
