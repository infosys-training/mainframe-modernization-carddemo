package com.carddemo.transactiontype;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.carddemo.transactiontype", "com.carddemo.common"})
@EntityScan(basePackages = "com.carddemo.common.entity")
public class TransactionTypeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionTypeServiceApplication.class, args);
    }
}
