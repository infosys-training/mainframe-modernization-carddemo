package com.carddemo.userauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.carddemo.userauth", "com.carddemo.common"})
@EntityScan(basePackages = "com.carddemo.common.entity")
public class UserAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserAuthServiceApplication.class, args);
    }
}
