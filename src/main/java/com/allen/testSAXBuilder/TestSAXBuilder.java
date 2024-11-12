package com.allen.testSAXBuilder;


import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;

/**
 * @Author: allen
 * @Date: 2024/11/6 20:10
 * @Description:
 **/
public class TestSAXBuilder {

    public static void main(String[] args) throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build("");
    }
}
