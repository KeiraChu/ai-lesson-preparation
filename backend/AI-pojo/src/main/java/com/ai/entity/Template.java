package com.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ：褚婧雯
 * @date ：2025/4/8 19:59
 * @description ：ppt模板类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Template {

    private String templateIndexId;
    private Integer pageCount;
    private String type;
    private String color;
    private String industry;
    private String style;
    private DetailImage detailImage;
    private String payType;
}
