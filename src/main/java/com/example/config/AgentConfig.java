package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import java.util.Map;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "agent")
@Data
public class AgentConfig {

    private Map<String, List<String>> severityRules;
}
