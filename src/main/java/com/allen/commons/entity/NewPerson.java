package com.allen.commons.entity;

public class NewPerson extends Person {

    public NewPerson(String personid, String name, String address, String tel, String email, String job, String company, String message, Integer code) {
        this.personid = personid;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.email = email;
        this.job = job;
        this.company = company;
        this.message = message;
        this.code = code;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
