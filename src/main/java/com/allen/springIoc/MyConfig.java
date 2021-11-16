package com.allen.springIoc;

import com.allen.commons.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Configuration
public class MyConfig {

    /**
     * @Lazy
     * - 懒加载失效问题：
     * 本例中在PersonService类中使用@Autowired自动注入Person
     * 由于PersonService不是懒加载的，在初始化PersonService的时候会调用getBean方法从Spring工厂中获取字段的bean对象，
     * 导致Person被加载实例化。
     * - 解决：
     * 需要在使用@Autowired的类上加上@Lazy，使其在启动时不被加载，不触发@Autowired注解依赖链的调用即可；
     *
     * @Scope
     * singleton 单例模式（默认）
     * prototype原型模式，每次获取都新建一个实例
     */
    @Lazy
    @Bean
    @Scope("prototype")
    public Person person() {
        return new Person();
    }
}
