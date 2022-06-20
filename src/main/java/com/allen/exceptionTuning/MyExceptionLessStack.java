package com.allen.exceptionTuning;

/**
 * @Author: allen
 * @Date: 2022/6/20 4:25 PM
 * @Description:
 */
public class MyExceptionLessStack extends MyException{

    /**
     * Throwable默认构造方法会调用fillInStackTrace()进行异常信息填充
     * @return Throwable
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    /**
     * 使自定义的异常不收集线程栈信息
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public MyExceptionLessStack(String message) {
        super(message);
    }

    public static void b() throws MyExceptionLessStack {
        c();
    }

    public static void c() throws MyExceptionLessStack {
        throw new MyExceptionLessStack("自定义异常...");
    }

    public static void a() throws MyExceptionLessStack {
        b();
    }

    public static void main(String[] args) {
        try {
            a();
        } catch (MyExceptionLessStack e) {
            e.printStackTrace();
        }
    }
}
