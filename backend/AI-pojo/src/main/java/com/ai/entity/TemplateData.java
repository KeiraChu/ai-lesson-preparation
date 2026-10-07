package com.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ：褚婧雯
 * @date ：2025/4/8 20:17
 * @description ：TODO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateData {
    private Integer total;
    private List<Template> records;
}
