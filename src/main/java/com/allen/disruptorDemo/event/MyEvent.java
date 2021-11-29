package com.allen.disruptorDemo.event;

/**
 * 自定义事件类【RingBuffer中的实际内容】
 */
public class MyEvent {

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

}
