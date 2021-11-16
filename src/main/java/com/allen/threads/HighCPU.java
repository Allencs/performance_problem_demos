package com.allen.threads;

import java.util.ArrayList;
import java.util.List;

public class HighCPU {

    public static void main(String[] args) {
        int threadCount = 100;
        final List<Thread> list = new ArrayList<>(threadCount);
        for(int i =0; i<threadCount; i++){
            Thread thread = new Thread(()->{
                while(true){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setName("cpuThread" + i);
            list.add(thread);
            thread.start();
        }

    }
}
