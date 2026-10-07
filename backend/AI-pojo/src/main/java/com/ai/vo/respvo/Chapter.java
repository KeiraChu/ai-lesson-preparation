package com.ai.vo.respvo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter {
    private String chapterTitle;
    private List<ChapterContent> chapterContents;

}