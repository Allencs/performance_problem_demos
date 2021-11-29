package com.allen.disruptorDemo;

import com.allen.disruptorDemo.event.MyEvent;
import com.allen.disruptorDemo.event.MyEventFactory;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DisruptorMain {

    // 消费者线程池
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2,
            60, TimeUnit.SECONDS,
            new ArrayBlockingQueue(1000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        // 创建事件工厂
        MyEventFactory myEventFactory = new MyEventFactory();
        // RingBuffer大小（必须2的N次方）
        int ringBufferSize = 1024;

        // 创建disruptor核心对象，
        Disruptor<MyEvent> disruptor = new Disruptor<>(myEventFactory, ringBufferSize,
                executor,
                ProducerType.SINGLE,
                new BlockingWaitStrategy());
        // 绑定相关的事件处理类【一个Handler会对应一个线程】
        disruptor.handleEventsWith(new MyEventHandler(1), new MyEventHandler(100), new MyEventHandler(101));
        disruptor.start();

        // 创建RingBuffer
        RingBuffer<MyEvent> ringBuffer = disruptor.getRingBuffer();
        // 创建生产者，并绑定RingBuffer
        MyEventProducer myEventProducer = new MyEventProducer(ringBuffer);
        // 指定缓冲区大小
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (int i = 1; i <= 100; i++) {
            byteBuffer.putLong(0, i);
            myEventProducer.onData(byteBuffer);
        }
        // 关闭Disruptor
        disruptor.shutdown();
        executor.shutdown();
    }
}
