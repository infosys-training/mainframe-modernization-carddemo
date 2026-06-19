package com.carddemo.authorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.carddemo.authorization", "com.carddemo.common"})
@EntityScan(basePackages = {"com.carddemo.common.entity", "com.carddemo.authorization.entity"})
public class AuthorizationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServiceApplication.class, args);
    }
}
