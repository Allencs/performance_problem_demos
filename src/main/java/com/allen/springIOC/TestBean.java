package com.allen.springIOC;

import com.allen.commons.entity.Person;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBean implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public static void main(String[] args) throws InterruptedException {
//        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        TestBean testBean = new TestBean();
        testBean.setApplicationContext(new ClassPathXmlApplicationContext("applicationContext.xml"));
        System.out.println("ApplicationContext | " + testBean.applicationContext.getDisplayName());
        Person person = testBean.applicationContext.getBean("person", Person.class);
        System.out.println(person.hashCode());
//        Thread.sleep(120000);
//        PersonController controller = testBean.applicationContext.getBean(PersonController.class);
//        controller.updatePersonInfo();
//        controller.showPersonInfo();

        System.out.println("----------------------------测试原型模式----------------------------");
        for (int i = 0; i < 5; i++) {
            Person person1 = testBean.applicationContext.getBean("person", Person.class);
            System.out.println(person1.hashCode());
        }
        System.out.println("----------------------------测试FactoryBean----------------------------");
        /**
         * 核心逻辑代码：org.springframework.beans.factory.support.AbstractBeanFactory#getObjectForBeanInstance(java.lang.Object, java.lang.String, java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition)
         * 在初始化Bean myPersonFactoryBean的时候，会判断是否为FactoryBean，
         * 如果是的话，会调用org.springframework.beans.factory.support.FactoryBeanRegistrySupport#getObjectFromFactoryBean(org.springframework.beans.factory.FactoryBean, java.lang.String, boolean)
         * 调用FactoryBean的getObject()方法返回对象实例，如果是单例，存入org.springframework.beans.factory.support.FactoryBeanRegistrySupport#factoryBeanObjectCache的缓存中
         */
        for (int i = 0; i < 5; i++) {
            Object object  = testBean.applicationContext.getBean("myPersonFactoryBean");
            System.out.println(object.hashCode());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
