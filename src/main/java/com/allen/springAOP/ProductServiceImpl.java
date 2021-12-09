package com.allen.springAOP;


import com.allen.testDynamicProxy.jdkProxy.ProductService;

public class ProductServiceImpl implements ProductService {

	public void addProduct(String productName) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("添加产品：" + productName);
	}
}
