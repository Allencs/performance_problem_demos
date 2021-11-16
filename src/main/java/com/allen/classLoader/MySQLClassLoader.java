package com.allen.classLoader;

import java.sql.Driver;
import java.util.Iterator;
import java.util.ServiceLoader;

public class MySQLClassLoader {

    public static void main(String[] args) {
        ServiceLoader<Driver> loader = ServiceLoader.load(Driver.class);
        Iterator<Driver> iterator = loader.iterator();
        while (iterator.hasNext()) {
            Driver driver = (Driver) iterator.next();
            System.out.println("driver:" + driver.getClass() + ",loader:" + driver.getClass().getClassLoader());
        }

        System.out.println("current thread contxtloader:" + Thread.currentThread().getContextClassLoader());

        System.out.println("ServiceLoader loader:" + ServiceLoader.class.getClassLoader());
    }

}
