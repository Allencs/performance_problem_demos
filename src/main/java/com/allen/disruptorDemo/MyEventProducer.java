package com.allen.disruptorDemo;

import com.allen.disruptorDemo.event.MyEvent;
import com.lmax.disruptor.RingBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class MyEventProducer {

    private final static Logger logger = LoggerFactory.getLogger(MyEventProducer.class);
    final RingBuffer<MyEvent> ringBuffer;

    public MyEventProducer(RingBuffer<MyEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer byteBuffer) {
        long sequence = ringBuffer.next();
        Long data = null;
        try {
            MyEvent myEvent = ringBuffer.get(sequence);
            data = byteBuffer.getLong(0);
            myEvent.setValue("message - " + data);
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            logger.info("消息组装并投放完毕，准备消费...");
            ringBuffer.publish(sequence);
        }
    }
}
