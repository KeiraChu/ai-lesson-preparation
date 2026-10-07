package com.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ：褚婧雯
 * @date ：2025/4/9 19:26
 * @description ：ppt生成key
 */
@Component
@ConfigurationProperties(prefix = "ai.ppt")
@Data
public class PPTProperties {
    private String APPID;
    private String APISecret;
}
