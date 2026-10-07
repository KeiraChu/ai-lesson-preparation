package com.ai.pptutils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutlineVo {
    // 标题
    private String title;
    // 副标题
    private String subTitle;
    // 章节
    private List<Chapter> chapters;
    // 结尾（约定为空）
    private String end = "";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    // 章节结构体
    public static class Chapter {
        // 章节名
        String chapterTitle;
        // 章节二级标题
        List<String> chapterContent;
    }
}