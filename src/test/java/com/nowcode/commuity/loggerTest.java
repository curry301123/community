package com.nowcode.commuity;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class loggerTest {

    private static final Logger logger = LoggerFactory.getLogger(loggerTest.class);

    @Test
    public void testLogger(){
        System.out.println(logger.getName());

        logger.debug("debugLog");
        logger.info("info log");
        logger.warn("warn log");
        logger.error("error log");
    }
}
