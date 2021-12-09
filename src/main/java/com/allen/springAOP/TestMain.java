package com.allen.springAOP;

import com.allen.testDynamicProxy.jdkProxy.ProductService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMain {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MyConfig.class);

		ProductService productService = (ProductService) ctx.getBean("productService");
		productService.addProduct("Iphone");
	}
}
