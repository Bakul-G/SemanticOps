package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.EnableKafka;

// import com.example.services.PromptTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableKafka
@Slf4j
public class EventDrivenApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(EventDrivenApp.class, args);
        // try {
        //     Thread.sleep(5000); // Wait for Kafka listener to initialize
        //     PromptTest promptTest = context.getBean(PromptTest.class);
        //     promptTest.testPrompt("Classify this: Payment service timeout repeated 5 times");
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
    }
}
