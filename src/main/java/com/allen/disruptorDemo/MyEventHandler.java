package com.allen.disruptorDemo;

import com.allen.disruptorDemo.event.MyEvent;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义事件消费处理逻辑
 */
public class MyEventHandler implements EventHandler<MyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MyEventHandler.class);
    private long serial;

    public MyEventHandler(long serial){
        this.serial = serial;
    }

    @Override
    public void onEvent(MyEvent event, long sequence, boolean endOfBatch) throws Exception {
        logger.info("消费者-{}:{}", this.serial, event.getValue());
    }
}
