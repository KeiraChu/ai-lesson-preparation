package com.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.orchestration")
public class AiServiceProperties {
    private String baseUrl = "http://localhost:8000";
    private String internalApiKey = "development-only";
    private Integer timeoutSeconds = 90;
}

