package com.ai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ：褚婧雯
 * @date ：2025/4/12 20:09
 * @description ：文件
 */
@Component
@ConfigurationProperties(prefix = "ai.file")
@Data
public class FileProperties {
    private String ipPort;
    private String uploadPath;
}
