package com.allen.commons.entity;

public class Person {
    private String personid;
    private String name;
    private String address;
    private String tel;
    private String email;
    private String job;
    private String company;
    private String message;
    private Integer code;


    @Override
    public String toString() {
        return "Person{" +
                "personid='" + personid + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                ", email='" + email + '\'' +
                ", job='" + job + '\'' +
                ", company='" + company + '\'' +
                ", message='" + message + '\'' +
                ", code=" + code +
                '}';
    }

    public String getPersonId() {
        return personid;
    }

    public void setPersonId(String personid) {
        this.personid = personid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}