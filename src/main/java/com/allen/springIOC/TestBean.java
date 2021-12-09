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
        for (int i = 0; i < 10; i++) {
            Person person1 = testBean.applicationContext.getBean("person", Person.class);
            System.out.println(person1.hashCode());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
