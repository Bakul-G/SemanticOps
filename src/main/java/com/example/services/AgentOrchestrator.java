package com.example.services;

import com.example.data.AlertEvent;
import com.example.data.EventDecision;
import com.example.repository.EventDecisionRepository;
import com.example.services.llmclinets.OpenAIClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AgentOrchestrator {

    private final OpenAIClient openAIClient;
    private final EventDecisionRepository repository;
    private final EmbeddingClient embeddingClient;
    private final ObjectMapper objectMapper;
    private final ActionExecutor actionExecutor;

    public AgentOrchestrator(OpenAIClient openAIClient,
                             EventDecisionRepository repository,
                             EmbeddingClient embeddingClient,
                             ActionExecutor actionExecutor) {
        this.openAIClient = openAIClient;
        this.repository = repository;
        this.embeddingClient = embeddingClient;
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
        this.actionExecutor = actionExecutor;
    }

    public void processEvent(AlertEvent event) {
        try {
            // 🔹 Step 1: Generate embedding for incoming event
            float[] queryEmbedding = embeddingClient.generateEmbedding(event.getMessage());

            // 🔹 Step 2: Fetch similar past events (vector search)
            String embeddingStr = toPgVector(queryEmbedding);
            
            List<EventDecision> pastEvents;
            try {
                pastEvents = repository.findTop5Similar(embeddingStr);
            } catch (Exception e) {
                pastEvents = List.of();
            }

            log.info("Past events found: {}", pastEvents.size());

            // 🔹 Step 3: Build context with timestamps
            String context = buildContext(pastEvents);

            // 🔹 Step 4: Build final prompt
            String prompt = buildUserPrompt(context, event.getMessage());

            // 🔹 Step 5: Call LLM
            String rawResponse = openAIClient.callLlm(prompt);

            // 🔹 Step 6: Extract JSON content
            String content = extractContent(rawResponse);

            // 🔹 Step 7: Parse JSON
            JsonNode json = objectMapper.readTree(content);

            String severity = json.path("severity").asText();
            String reason = json.path("reason").asText();

            // 🔹 Step 8: Print output
            log.info("EventId: {}", event.getEventId());
            log.info("Severity: {}", severity);
            log.info("Reason: {}", reason);

            // 🔹 Step 9: Save decision with embedding
            saveDecision(event, severity, reason);

            actionExecutor.execute(severity, reason);

        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage());
        }
    }

    private String buildContext(List<EventDecision> pastEvents) {
        if (pastEvents.isEmpty()) {
            log.info("PastEvent: {}", pastEvents);
            return "No similar past events.";}

        StringBuilder context = new StringBuilder("Past events:\n");

        for (EventDecision e : pastEvents) {
            context.append("[")
                    .append(e.getTimestamp())
                    .append("] ")
                    .append(e.getMessage())
                    .append(" → ")
                    .append(e.getSeverity())
                    .append("\n");
        }

        return context.toString();
    }

    private String buildUserPrompt(String context, String eventMessage) {
        return context +
                "\nNow classify:\n" +
                eventMessage;
    }

    private String extractContent(String rawResponse) throws Exception {
        JsonNode root = objectMapper.readTree(rawResponse);

        return root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }

    private void saveDecision(AlertEvent event, String severity, String reason) {

        float[] embedding = embeddingClient.generateEmbedding(event.getMessage());

        EventDecision decision = new EventDecision();
        decision.setEventId(event.getEventId());
        decision.setMessage(event.getMessage());
        decision.setSeverity(severity);
        decision.setReason(reason);
        decision.setTimestamp(event.getTimestamp());
        decision.setEmbedding(embedding);

        repository.save(decision);
    }

    private String toPgVector(float[] embedding) {

        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);

            if (i < embedding.length - 1) {
                sb.append(",");
            }
        }

        sb.append("]");

        return sb.toString();
    }  
}