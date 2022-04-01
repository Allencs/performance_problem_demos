## 代理对象创建流程

### AOP前期准备工作
```text
// AOP前期准备工作
org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessBeforeInstantiation

// 判断是否是基础Bean
org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator#isInfrastructureClass
// 传入正在创建的Bean对象的class中是不是有@Aspect切面的注解信息｜有没有切面注解   &&  不是有AJC进行编译的
org.springframework.aop.aspectj.annotation.AbstractAspectJAdvisorFactory#isAspect

org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator#shouldSkip
	// 找出所有advisor相关的bean
	org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors
		// 找出所有接口实现的advisor
		org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findCandidateAdvisors

			org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper#findAdvisorBeans
			// 去容器中获取到所有的切面信息保存到缓存中【遍历beanNames】
		org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors
			// ！！！获取切面Bean的Advice，【遍历Bean中没有标注@PointCut的所有方法】封装成
			org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisors
				// 1、切面的方法上构建 切点表达式对象 2、实例化我们的切面通知对象【封装成：InstantiationModelAwarePointcutAdvisorImpl】
				org.springframework.aop.aspectj.annotation.ReflectiveAspectJAdvisorFactory#getAdvisor


// 创建代理对象
org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#postProcessAfterInitialization
	
	org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary
		// 根据PointCut匹配需要，并封装成
		org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#getAdvicesAndAdvisorsForBean
			// 找到合适的增强器对象
			org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findEligibleAdvisors
				// 找到ioc容器中候选的通知[找出所有advisor相关的bean]
				org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors
					// 
					org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findCandidateAdvisors
						// 探测器字段缓存中cachedAdvisorBeanNames 是用来保存我们的Advisor全类名
						org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper#findAdvisorBeans
					// 去容器中获取到所有的切面信息保存到缓存中
					org.springframework.aop.aspectj.annotation.BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors
				// 判断我们的通知能不能作用到当前的类上
				org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator#findAdvisorsThatCanApply
					// ！！！从候选的通知器中找到合适我们正在创建的实例对象[遍历Advisors进行匹配]
					org.springframework.aop.support.AopUtils#findAdvisorsThatCanApply
						// 真正的判断我们的事务增强器是不是我们合适的
						org.springframework.aop.support.AopUtils#canApply(org.springframework.aop.Advisor, java.lang.Class<?>, boolean)
							// ！！！PointCut匹配 【遍历目标对象的所有方法进行匹配】
							org.springframework.aop.support.AopUtils#canApply(org.springframework.aop.Pointcut, java.lang.Class<?>, boolean)
```

### 代理对象调用流程
```text
/**
 * 代理对象调用流程【场景：@Before➕@Around】
 */
// 若执行代理对象的equals、hashCode、DecoratingProxy方法不走代理逻辑
org.springframework.aop.framework.JdkDynamicAopProxy#invoke
	// 把我们的aop的advisor 转化为拦截器，之后调用会走缓存【只有MethodInterceptor有invoke方法】
	org.springframework.aop.framework.AdvisedSupport#getInterceptorsAndDynamicInterceptionAdvice
		// 第一次需要创建
		org.springframework.aop.framework.DefaultAdvisorChainFactory#getInterceptorsAndDynamicInterceptionAdvice
			// 创建MethodInterceptors
			org.springframework.aop.framework.adapter.AdvisorAdapterRegistry#getInterceptors
				org.springframework.aop.framework.adapter.DefaultAdvisorAdapterRegistry#getInterceptors
					// 调用对应的适配器【MethodBeforeAdviceAdapter、AfterReturningAdviceAdapter、ThrowsAdviceAdapter】进行创建
					org.springframework.aop.framework.adapter.MethodBeforeAdviceAdapter#getInterceptor
	// /创建一个方法调用对象
	MethodInvocation invocation = org.springframework.aop.framework.ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);

	//调用执行【进行链式调用】
	retVal = invocation.proceed()
		// 链式递归调用
		org.springframework.aop.framework.ReflectiveMethodInvocation#proceed
			// 第一次总是ExposeInvocationInterceptor【默认添加的】，为了记录当前执行的拦截器【ReflectiveMethodInvocation】
			org.springframework.aop.interceptor.ExposeInvocationInterceptor#invoke
			
			// 调用对应 Advice的invoke方法
			org.springframework.aop.aspectj.AspectJAroundAdvice#invoke
					// 只有AspectJAroundAdvice才有
					/**
					 * 将 MethodInvocation【实质上就是ReflectiveMethodInvocation对象】 封装成 MethodInvocationProceedingJoinPoint
					 *
					 * 在Around 通知自定义逻辑调用joinPoint.proceed(args)时，就是调用的MethodInvocationProceedingJoinPoint的proceed方法
					 * 会将之前传入的MethodInvocation动态进行clone，最终调用的是克隆后的MethodInvocation 的proceed 方法
					 */
					org.springframework.aop.aspectj.AspectJAroundAdvice#lazyGetProceedingJoinPoint
						new MethodInvocationProceedingJoinPoint(rmi)；
							// 初始化静态变量
							private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
								addDiscoverer(new StandardReflectionParameterNameDiscoverer());
								addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
							// 初始化方法，接收传入的MethodInvocation【实质上就是ReflectiveMethodInvocation对象】
							this.methodInvocation = methodInvocation; 【ReflectiveMethodInvocation】

					org.springframework.aop.aspectj.AbstractAspectJAdvice#getJoinPointMatch(org.springframework.aop.ProxyMethodInvocation)

					org.springframework.aop.aspectj.AbstractAspectJAdvice#invokeAdviceMethod(org.aspectj.lang.JoinPoint, org.aspectj.weaver.tools.JoinPointMatch, java.lang.Object, java.lang.Throwable)

						org.springframework.aop.aspectj.AbstractAspectJAdvice#invokeAdviceMethodWithGivenArgs
							// 通过反射执行 Advice动作
							java.lang.reflect.Method#invoke
								// 在AspectJAroundAdvice通知方法内调用joinPoint.proceed(args)【lazyGetProceedingJoinPoint创建
								// 的MethodInvocationProceedingJoinPoint的proceed方法】
								org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint#proceed(java.lang.Object[])
									// 创建MethodInvocation克隆【Around的核心所在，相当于套了一层循环，将Before After等Advice包裹住】
									// 每次调用都会进行clone操作
									org.springframework.aop.framework.ReflectiveMethodInvocation#invocableClone(java.lang.Object...)
										// 执行克隆ReflectiveMethodInvocation对象的proceed【循环执行其他Advice动作】
										org.springframework.aop.framework.ReflectiveMethodInvocation#proceed
											// 反射执行被增强的原方法
											org.springframework.aop.framework.ReflectiveMethodInvocation#invokeJoinpoint

											//·····【环绕通知，执行原方法执行后的逻辑】
			// 最终回退到了ExposeInvocationInterceptor类的invoke方法，来执行finally块中的逻辑：【将ThreadLocal<MethodInvocation> invocation至空】
			org.springframework.aop.interceptor.ExposeInvocationInterceptor#invoke

	// ····执行接下去的逻辑

```


