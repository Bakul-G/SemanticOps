package com.example.services.llmclinets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.data.LlmClient;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Service
@Primary
@Slf4j
public class OpenAIClient implements LlmClient{

    private final WebClient webClient;

    public OpenAIClient(@Value("${openai.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String callLlm(String eventMessage) {
        log.info("Calling openAi api, eventMessage: {}", eventMessage);

        // SYSTEM + USER prompt (optimized for low tokens)
        List<Map<String, String>> messages = List.of(
            Map.of(
                "role", "system",
                "content", "Return ONLY valid JSON.\n" +
                        "Schema:{\"severity\":\"LOW|MEDIUM|HIGH\",\"reason\":\"short\"}.\n" +
                        "Do not add extra fields. Do not change keys."
            ),
            Map.of(
                "role", "user",
                "content", "Classify severity. Event: " + eventMessage
            )
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", messages,
                "temperature", 0,
                "max_tokens", 100,
                "response_format", Map.of("type", "json_object")
        );

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(error -> new RuntimeException("OpenAI Error: " + error))
                )
                .bodyToMono(String.class)
                .block();
    }
}
