package com.sonet.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SonetCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonetCoreApplication.class, args);
    }

}
