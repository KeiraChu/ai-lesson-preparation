package com.ai.client;

import com.ai.properties.AiServiceProperties;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;
import java.io.IOException;

@Component
public class AiOrchestrationClient {
    private final WebClient webClient;
    private final AiServiceProperties properties;

    public AiOrchestrationClient(WebClient.Builder builder, AiServiceProperties properties) {
        this.properties = properties;
        this.webClient = builder.baseUrl(properties.getBaseUrl()).build();
    }

    public Map<String, Object> generateLessonPlan(Map<String, Object> request) {
        return webClient.post()
                .uri("/v1/workflows/lesson-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Internal-Api-Key", properties.getInternalApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();
    }

    public Map<String, Object> ingestDocument(Map<String, Object> request) {
        return webClient.post()
                .uri("/v1/knowledge/documents")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Internal-Api-Key", properties.getInternalApiKey())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();
    }

    public Map<String, Object> ingestFile(MultipartFile file, Long userId, String knowledgeBaseId) throws IOException {
        MultipartBodyBuilder body = new MultipartBodyBuilder();
        body.part("user_id", String.valueOf(userId));
        body.part("knowledge_base_id", knowledgeBaseId);
        body.part("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        }).contentType(MediaType.parseMediaType(file.getContentType() == null ? "application/octet-stream" : file.getContentType()));
        return webClient.post()
                .uri("/v1/knowledge/files")
                .header("X-Internal-Api-Key", properties.getInternalApiKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body.build())
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
                .block();
    }
}
