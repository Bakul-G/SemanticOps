package com.example.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class EmbeddingClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmbeddingClient(@Value("${openai.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/embeddings")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public float[] generateEmbedding(String text) {
        try {
            Map<String, Object> body = Map.of(
                    "model", "text-embedding-3-small",
                    "input", text
            );

            String response = webClient.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingArray = root.path("data").get(0).path("embedding");

            float[] embedding = new float[embeddingArray.size()];

            for (int i = 0; i < embeddingArray.size(); i++) {
                embedding[i] = (float) embeddingArray.get(i).asDouble();
            }

            return embedding;

        } catch (Exception e) {
            throw new RuntimeException("Embedding error: " + e.getMessage());
        }
    }
}
