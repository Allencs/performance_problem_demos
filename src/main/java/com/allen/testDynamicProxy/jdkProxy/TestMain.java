package com.allen.testDynamicProxy.jdkProxy;

import sun.misc.ProxyGenerator;
import java.io.FileOutputStream;

public class TestMain {

    public static void main(String[] args) {
        ProductService productService = new ProductServiceImpl();
        ProductService proxy = (ProductService) new JdkInvocationHandler().getInstance(productService);
        proxy.addProduct("iphone");

        // 这里我们将jdk生成的代理类输出了出来，方便后面分析使用
        byte[] bytes = ProxyGenerator.generateProxyClass("$Proxy0",new Class[]{productService.getClass()});
        try {
            FileOutputStream os = new FileOutputStream("Proxy0.class");
            os.write(bytes);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
