package com.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ：褚婧雯
 * @date ：2025/4/13 16:50
 * @description ：
 */
@Component
@ConfigurationProperties(prefix = "ai.analyze")
@Data
public class AnalyzeProperties {
    private String apiKey;
    private String appId;
}
