package com.allen.springIOC;

import com.allen.models.Person;
import com.allen.utils.UuidUtil;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @Author: allen
 * @Date: 2022/7/13 16:34
 * @Description:
 */

@Component
public class MyPersonFactoryBean implements FactoryBean<Person> {

    @Override
    public Person getObject() {
        Person person = new Person();
        person.setPersonId(UuidUtil.getStrUuid());
        person.setCode(100);
        person.setName("testFactoryBean");
        person.setCompany("PerfMa");
        return person;
    }

    @Override
    public Class<?> getObjectType() {
        return Person.class;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
//        return false;
    }
}
