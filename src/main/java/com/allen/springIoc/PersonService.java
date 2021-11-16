package com.allen.springIoc;

import com.allen.commons.entity.Person;
import com.allen.commons.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    @Autowired
    private Person person;

    PersonService() {
        System.out.println(this.getClass() + " ｜ 实例初始化...");
    }

    public String getPersonInfo() {
        return person.toString();
    }

    public void setPersonInfo() {
        person.setCode(200);
        person.setJob("PerformanceTestEngineer");
        person.setCompany("Perfma");
        person.setName("perfma666");
        person.setMessage("Congratulation! You got the message!");
        person.setPersonId(UuidUtil.getStrUuid());
        person.setAddress("黄河边大桥上");
        person.setEmail("nb666@perfma.com");
        person.setTel("88888888");
    }
}
