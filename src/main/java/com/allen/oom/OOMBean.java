package com.allen.oom;

import java.io.Serializable;

public class OOMBean implements Serializable {

    String flag;
    int number;

    public OOMBean(String str, int i) {
        this.flag = "oom test";
        this.number = i;
    }
}
