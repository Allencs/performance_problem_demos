package com.allen.tmp;

import com.allen.commons.entity.Person;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: allen
 * @Date: 2022/7/7 4:13 PM
 * @Description:
 */
public class TestObjectMapper {

    private final ThreadPoolExecutor executor;

    public TestObjectMapper() {
        this.executor = new ThreadPoolExecutor(10, 10,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public static void main(String[] args) {
        String personStr = "{\"personid\":null, \"name\":\"TestGroovy270\", \"address\":null, \"tel\":null, \"email\":null, \"job\":null, \"company\":null, \"message\":null, \"code\":666}";
        new TestObjectMapper().executor.submit(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ObjectMapper om = new ObjectMapper();
                    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
                    om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    try {
                        Person person = om.readValue(personStr, Person.class);
                        om = null;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}
