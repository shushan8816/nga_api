package com.nga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class NgaApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(NgaApiApplication.class, args);
    }

}
