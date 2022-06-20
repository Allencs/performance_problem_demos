package com.allen.exceptionTuning;

/**
 * main() -> a() -> b() -> c()
 */
public class MyException extends Exception {
    /**
     * 使自定义的异常不收集线程栈信息
     * @return this
     */

    public MyException(String message) {
        super(message);
    }

    public static void b() throws MyException {
        c();
    }

    /**
     * fillInStackTrace每次执行的时候，会清空原来的栈内的trace信息。然后在当前的调用位置处重新建立trace信息，
     * 所以在方法a2()中printStackTrace的执行结果没有c()方法的调用轨迹
     * @throws MyException 自定义异常对象
     */
    public static void b2() throws MyException {
        try {
            c();
        } catch (Exception e) {
            e.printStackTrace();
//            throw new MyException(e.getMessage());
//            throw e;
            throw (MyException)e.fillInStackTrace();
        }
    }

    public static void c() throws MyException {
        throw new MyException("自定义异常...");
    }

    public static void a() throws MyException {
        b();
    }

    public static void a2() throws MyException {
        b2();
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            a();
        } catch (MyException e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------");
        Thread.sleep(1000);
        try {
            a2();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
