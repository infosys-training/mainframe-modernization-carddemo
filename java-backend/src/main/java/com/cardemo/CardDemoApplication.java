package com.cardemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CardDemo Application - Migrated from COBOL/CICS mainframe to Java 17 / Spring Boot.
 *
 * Original mainframe application provided credit card management functionality
 * including account management, card operations, transaction processing,
 * bill payments, reporting, and user administration.
 */
@SpringBootApplication
public class CardDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardDemoApplication.class, args);
    }
}
