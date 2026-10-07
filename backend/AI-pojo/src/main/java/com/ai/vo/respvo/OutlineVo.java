package com.ai.vo.respvo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author ：褚婧雯
 * @date ：2025/4/9 8:53
 * @description ：大纲Vo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutlineVo {
    private String title;
    private String subTitle;
    private List<Chapter> chapters;
}
