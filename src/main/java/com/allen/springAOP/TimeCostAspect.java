package com.allen.springAOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Aspect
@Component
public class TimeCostAspect {

	@Pointcut("execution(* com.allen.springAOP.ProductServiceImpl.*(..))")
	public void pointCut(){};

	@Around(value = "pointCut()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		//获取方法参数值数组
		Object[] args = joinPoint.getArgs();
		//得到其方法签名
//		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		//获取方法参数类型数组
//		Class[] paramTypeArray = methodSignature.getParameterTypes();
		System.out.println("请求参数为:" + args);
		//动态修改其参数
		//注意，如果调用joinPoint.proceed()方法，则修改的参数值不会生效，必须调用joinPoint.proceed(Object[] args)
		Object result = joinPoint.proceed(args);
		System.out.println("响应结果为: " + result);
		//如果这里不返回result，则目标对象实际返回值会被置为null
		return result;
	}

	@Before("execution(* com.allen.springAOP.ProductServiceImpl.*(..))")
	public void methodBefore(JoinPoint joinPoint) {
		String methodName = joinPoint.getSignature().getName();
		System.out.println("执行目标方法【"+methodName+"】的<前置通知>,入参"+ Arrays.asList(joinPoint.getArgs()));
	}
}
