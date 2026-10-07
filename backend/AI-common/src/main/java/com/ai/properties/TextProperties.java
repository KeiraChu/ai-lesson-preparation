package com.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ：褚婧雯
 * @date ：2025/4/7 20:05
 * @description ：大模型的key
 */
@Component
@ConfigurationProperties(prefix = "ai.text")
@Data
public class TextProperties {
    private String apiKey;
    private String appId;
}

