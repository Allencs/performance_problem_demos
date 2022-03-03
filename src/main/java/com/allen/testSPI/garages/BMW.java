package com.allen.testSPI.garages;

import com.allen.testSPI.Car;

/**
 * @Author: allen
 * @Date: 2022/2/27 8:34 PM
 * @Description:
 **/
public class BMW implements Car {
    @Override
    public String getCarName() {
        return "BMW";
    }
}
