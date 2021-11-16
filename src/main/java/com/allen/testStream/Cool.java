package com.allen.testStream;

import java.util.Objects;

public class Cool {

    private String name;

    private String code;

    private String pp;

    private String cc;

    @Override
    public String toString() {
        return "Cool{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", pp='" + pp + '\'' +
                ", cc='" + cc + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cool cool = (Cool) o;
        return Objects.equals(name, cool.name) && Objects.equals(code, cool.code) && Objects.equals(pp, cool.pp) && Objects.equals(cc, cool.cc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code, pp, cc);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }




}
