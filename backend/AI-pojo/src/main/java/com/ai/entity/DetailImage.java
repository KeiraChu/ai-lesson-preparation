package com.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：褚婧雯
 * @date ：2025/4/8 20:01
 * @description ：ppt模板图片
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailImage {
    private String titleCoverImageLarge;
    private String titleCoverImage;
    private String catalogueCoverImage;
    private String chapterCoverImage;
    private String contentCoverImage;
    private String endCoverImage;
}
