package com.allen.testDynamicProxy.jdkProxy;

public class ProductServiceImpl implements ProductService{
    @Override
    public void addProduct(String productName) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("添加产品：" + productName);
    }
}
