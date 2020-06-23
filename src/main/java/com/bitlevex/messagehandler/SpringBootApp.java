package com.bitlevex.messagehandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/*@SpringBootApplication
public class SpringBootApp extends SpringBootServletInitializer {
}*/

@SpringBootApplication
public class SpringBootApp {
    public static void main(String[] args) {
        new SpringApplication(SpringBootApp.class).run(args);
    }
}

