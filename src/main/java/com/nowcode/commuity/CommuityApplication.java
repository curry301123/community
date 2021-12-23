package com.nowcode.commuity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommuityApplication {

    @PostConstruct
    public void init(){
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommuityApplication.class, args);
    }

}
