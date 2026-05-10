package com.example.services;

import org.springframework.stereotype.Component;

import com.example.config.AgentConfig;

import java.util.Map;
import java.util.List;

@Component
public class PromptBuilder {

    private final AgentConfig config;

    public PromptBuilder(AgentConfig config) {
        this.config = config;
    }

    public String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Return ONLY valid JSON.\n");
        prompt.append("Schema:{\"severity\":\"LOW|MEDIUM|HIGH\",\"reason\":\"short\"}\n");

        prompt.append("Severity rules:\n");

        for (Map.Entry<String, List<String>> entry : config.getSeverityRules().entrySet()) {
            prompt.append("- ").append(entry.getKey()).append(": ");
            prompt.append(String.join(", ", entry.getValue()));
            prompt.append("\n");
        }

        prompt.append("Do not add extra fields.");

        return prompt.toString();
    }
}
