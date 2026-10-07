package com.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ：褚婧雯
 * @date ：2025/4/11 21:13
 * @description ：practice
 */
@Component
@ConfigurationProperties(prefix = "ai.practice")
@Data
public class PracticeProperties {
    private String apiKey;
    private String appId;
}
