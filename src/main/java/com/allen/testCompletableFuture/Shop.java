package com.allen.testCompletableFuture;

import java.util.Random;

/**
 * @Author: allen
 * @Date: 2022/2/10 11:37 上午
 * @Description:
 */

public class Shop {

    private String name;
    private Random random = new Random();

    public Shop(String name) {
        this.name = name;
    }

    /**
     * 根据产品名查找价格
     */
    public double getPrice(String product) {
        return calculatePrice(product);
    }

    /**
     * 计算价格
     *
     * @return price in double
     */
    private double calculatePrice(String product) {
        delay();
        //random.nextDouble()随机返回折扣
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    /**
     * 通过睡眠模拟其他耗时操作
     */
    private void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}

