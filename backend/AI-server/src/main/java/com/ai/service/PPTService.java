package com.ai.service;

import com.ai.result.Result;
import com.ai.vo.reqvo.PPTVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/8 19:24
 * @description ：ppt生成接口
 */
public interface PPTService {
    /**
     * 查询ppt模板
     * @return
     */
    Result getTemplate(HashMap<String,String> info);

    /**
     * 根据简短提示词生成大纲
     * @param info
     * @return
     */
    Result genOutline(HashMap<String,String> info);

    /**
     * 根据大纲生成ppt
     * @param pptVo
     * @return
     */
    Result genPPT(PPTVo pptVo);

    /**
     * 查询ppt生成进度
     * @param sid
     * @return
     */
    Result getPPTBySid(String sid);
}
