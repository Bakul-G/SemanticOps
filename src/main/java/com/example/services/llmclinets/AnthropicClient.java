// package com.example.services.llmclinets;

// import java.util.List;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;

// import com.example.data.LlmClient;

// @Service
// public class AnthropicClient implements LlmClient {

//     private final WebClient webClient;

//     public AnthropicClient(@Value("${anthropic.api-key}#{null}") String apiKey) {
//         this.webClient = WebClient.builder()
//                 .baseUrl("https://api.anthropic.com/v1/messages")
//                 .defaultHeader("x-api-key", apiKey)
//                 .defaultHeader("anthropic-version", "2023-06-01")
//                 .build();
//     }

//     public String callLlm(String prompt) {
//         Map<String, Object> body = Map.of(
//                 "model", "claude-3-sonnet-20240229",
//                 "max_tokens", 200,
//                 "messages", List.of(
//                         Map.of("role", "user", "content", prompt)
//                 )
//         );

//         return webClient.post()
//                 .bodyValue(body)
//                 .retrieve()
//                 .onStatus(
//                     status -> status.isError(),
//                     response -> response.bodyToMono(String.class)
//                         .map(errorBody -> new RuntimeException("Error: " + errorBody))
//                 )
//                 .bodyToMono(String.class)
//                 .block();
//     }
// }