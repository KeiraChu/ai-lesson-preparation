package com.ai.controller;

import com.ai.client.AiOrchestrationClient;
import com.ai.context.UserContext;
import com.ai.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiWorkflowController {
    private final AiOrchestrationClient client;

    public AiWorkflowController(AiOrchestrationClient client) {
        this.client = client;
    }

    @PostMapping("/lesson-plan")
    public Result<Map<String, Object>> generateLessonPlan(@RequestBody Map<String, Object> request) {
        Map<String, Object> safeRequest = new HashMap<>(request);
        safeRequest.put("user_id", String.valueOf(UserContext.getUserId()));
        return Result.success(client.generateLessonPlan(safeRequest));
    }
}

