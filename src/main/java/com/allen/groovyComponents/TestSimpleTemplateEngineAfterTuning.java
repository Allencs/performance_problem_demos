package com.allen.groovyComponents;

import com.allen.models.Person;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class TestSimpleTemplateEngineAfterTuning {

    private final static Logger log = LoggerFactory.getLogger(TestSimpleTemplateEngineAfterTuning.class);
    private final static ConcurrentHashMap<String, Template> templateCaches = new ConcurrentHashMap<>();
    private final static ArrayList strTemplates = new ArrayList();

    static {
        strTemplates.add("${user.name}");
        strTemplates.add("${user.code}");
        strTemplates.add("${user.company}");
        strTemplates.add("${user.address}");
        strTemplates.add("${user.message}");
    }

    public static Template getTemplate(String placeHolder) throws IOException, ClassNotFoundException {
        Template template = templateCaches.get(placeHolder);
        if (template != null) return template;
        template = new SimpleTemplateEngine().createTemplate(placeHolder);
        templateCaches.put(placeHolder, template);
        return template;
    }

    public static String generateMessage(Map map, String placeHolder) {
        String msg = null;
        try {
            msg = getTemplate(placeHolder).make(map).toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (; ; ) {
                    Map<String, Object> map = new HashMap<>();
                    int nameSuffix = new Random().nextInt(900) + 100;
                    int index = new Random().nextInt(strTemplates.size());
                    Person userDo = new Person();
                    userDo.setName("TestGroovy" + nameSuffix);
                    userDo.setCode(666);
                    map.put("user", userDo);
                    String placeHolder = (String) strTemplates.get(index);
                    String userName = generateMessage(map, placeHolder);
                    log.info(placeHolder + ": " + userName + Thread.currentThread().getName());
                }

            }).start();
        }
    }
}
