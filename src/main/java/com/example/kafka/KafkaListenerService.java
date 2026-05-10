package com.example.kafka;

import com.example.data.AlertEvent;
import com.example.services.AgentOrchestrator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    private final AgentOrchestrator orchestrator;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @KafkaListener(topics = "alerts-topic", groupId = "agent-group")
    public void consume(String message) {
        try {
            AlertEvent event = objectMapper.readValue(message, AlertEvent.class);
            log.info("Received event: {}", event);
            orchestrator.processEvent(event);
        } catch (Exception e) {
            log.error("Failed to parse message: {}", e.getMessage());
        }
    }
}
