package com.nowcode.commuity;

import com.nowcode.commuity.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void text1(){
        mailClient.sendMail("506015655@qq.com","Test","welcome");
    }

    @Test
    public void text2(){
        Context context = new Context();
        context.setVariable("username","sunday");

        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);

        mailClient.sendMail("506015655@qq.com","html",process);
    }
}
