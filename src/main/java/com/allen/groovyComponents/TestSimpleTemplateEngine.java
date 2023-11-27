package com.allen.groovyComponents;

import com.allen.models.Person;
import groovy.text.SimpleTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class TestSimpleTemplateEngine {

    private final static Logger log = LoggerFactory.getLogger(TestSimpleTemplateEngine.class);
    private final static ArrayList strTemplates = new ArrayList();

    static {
        strTemplates.add("${user.name}");
        strTemplates.add("${user.code}");
        strTemplates.add("${user.company}");
        strTemplates.add("${user.address}");
        strTemplates.add("${user.message}");
    }
    public static String generateMessage(Map map, String placeHolder) {
        String msg = null;
        try {
            msg = new SimpleTemplateEngine().createTemplate(placeHolder).make(map).toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

//        map.put("name","perfma$$$");
//        Object name = new SimpleTemplateEngine().createTemplate("${name}").make(map);
//        System.out.println("ss: " + name.toString());

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (;;) {
                    Map<String,Object> map = new HashMap<>();
                    int nameSuffix = new Random().nextInt(900) + 100;
                    int index = new Random().nextInt(strTemplates.size());
                    // 这里随便整个POJO都行，只要相关的属性对的上就行
                    Person userDo = new Person();
                    userDo.setName("TestGroovy" + nameSuffix);
                    userDo.setCode(666);
                    // 添加域对象
                    map.put("user",userDo);
                    String placeHolder = (String) strTemplates.get(index);
                    String userName = generateMessage(map, placeHolder);
                    log.info(placeHolder + ": " + userName + Thread.currentThread().getName());
                }
            }).start();
        }

    }

}
