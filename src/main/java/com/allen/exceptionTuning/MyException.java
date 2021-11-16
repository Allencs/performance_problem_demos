package com.allen.exceptionTuning;

public class MyException extends Exception {
    /**
     * 使自定义的异常不收集线程栈信息
     * @return this
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public MyException(String message) {
        super(message);
    }

    public static void b() throws MyException {
        c();
    }

    public static void c() throws MyException {
        throw new MyException("自定义异常...");
    }

    public static void a() throws MyException {
        b();
    }

    public static void main(String[] args) {
        try {
            a();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
