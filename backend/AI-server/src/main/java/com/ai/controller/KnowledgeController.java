package com.ai.controller;

import com.ai.client.AiOrchestrationClient;
import com.ai.context.UserContext;
import com.ai.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {
    private final AiOrchestrationClient client;

    public KnowledgeController(AiOrchestrationClient client) {
        this.client = client;
    }

    @PostMapping("/documents")
    public Result<Map<String, Object>> ingest(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "knowledgeBaseId", defaultValue = "default") String knowledgeBaseId
    ) throws IOException {
        if (file.isEmpty() || file.getSize() > 20L * 1024 * 1024) {
            throw new IllegalArgumentException("文件为空或超过 20MB 限制");
        }
        return Result.success(client.ingestFile(file, UserContext.getUserId(), knowledgeBaseId));
    }
}
