package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableKafka
@Slf4j
public class EventDrivenApp {

    public static void main(String[] args) {
        SpringApplication.run(EventDrivenApp.class, args);
    }
}
