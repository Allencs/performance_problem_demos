package com.allen;

import java.util.ArrayList;
import java.util.List;

public class TestListCapacity {

    public static void testAdd(List list, Object object) {
        list.add(object);
    }

    public static void doTest() {
        for (;;) {
            ArrayList arrayList = new ArrayList();
            do {
                Object obj = new Object();
                testAdd(arrayList, obj);
            } while (arrayList.size() < 1000000);

            arrayList.clear();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> doTest()).start();
        }
    }

}
