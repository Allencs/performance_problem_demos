package com.allen.testSPI;

import java.util.ServiceLoader;

/**
 * @Author: allen
 * @Date: 2022/2/27 8:35 PM
 * @Description:
 **/
public class TestMain {
    public static void main(String[] args) {
        ServiceLoader<Car> cars = ServiceLoader.load(Car.class);
        for (Car car : cars) {
            System.out.println(car.getCarName());
        }
    }
}
