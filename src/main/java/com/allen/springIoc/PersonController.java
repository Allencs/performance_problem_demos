package com.allen.springIoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    PersonController() {
        System.out.println(this.getClass() + " ｜ 实例初始化...");
    }

    public void showPersonInfo() {
        System.out.println(personService.getPersonInfo());
    }

    public void updatePersonInfo() {
        personService.setPersonInfo();
    }

}
