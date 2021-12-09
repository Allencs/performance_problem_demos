package com.allen.springAOP;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
@ComponentScan(basePackages = "com.allen.springAOP")
public class MyConfig {

	@Bean
	public ProductServiceImpl productService() {
		return new ProductServiceImpl();
	}
}
