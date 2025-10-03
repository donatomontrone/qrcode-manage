package com.example.qrapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class QrAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrAppApplication.class, args);
    }
}