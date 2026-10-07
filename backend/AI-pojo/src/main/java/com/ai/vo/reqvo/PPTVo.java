package com.ai.vo.reqvo;

import com.ai.vo.respvo.OutlineVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：褚婧雯
 * @date ：2025/4/9 9:11
 * @description ：生成ppt时的请求Vo
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PPTVo {
    private OutlineVo outline;
    private String language;
    private String templateId;
    private String query;
    private Long id;
}
