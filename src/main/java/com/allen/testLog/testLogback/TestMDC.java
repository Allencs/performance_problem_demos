package com.allen.testLog.testLogback;

import com.allen.commons.utils.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TestMDC {

    private final static Logger logger = LoggerFactory.getLogger(TestMDC.class);

    public void testMDC() {

        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                MDC.clear();
                MDC.put("uuid", UuidUtil.getStrUuid());
                logger.info("[{}]测试 MDC", Thread.currentThread().getName());
                MDC.remove("uuid");
            }).start();
        }

    }

    public static void main(String[] args) {
        new TestMDC().testMDC();
    }
}
