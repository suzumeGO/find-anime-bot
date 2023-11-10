package com.example.findanime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FindAnimeApplicationTests {
    @Autowired
    Bot bot;

    @Test
    void contextLoads() {
    }

    @Test
    public void timeFormatTest() {
        Assertions.assertEquals("00:08", ReflectionTestUtils.invokeMethod(bot, "setFormat", 8.330499999999999));
        Assertions.assertEquals("05:02", ReflectionTestUtils.invokeMethod(bot, "setFormat", 302.08));
        Assertions.assertEquals("00:22", ReflectionTestUtils.invokeMethod(bot, "setFormat", 21.9625));
    }

}
