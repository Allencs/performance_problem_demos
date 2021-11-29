package com.allen.disruptorDemo.event;

import com.lmax.disruptor.EventFactory;

/**
 * Disruptor利用此事件工厂创建自定义的事件对象
 */
public class MyEventFactory implements EventFactory<MyEvent> {
    @Override
    public MyEvent newInstance() {
        return new MyEvent();
    }
}
