package com.ai.service;

import com.ai.result.Result;

import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/11 21:04
 * @description ：PracticeService
 */
public interface PracticeService {

    /**
     * 根据教案生成练习题
     * @param info
     * @return
     */
    Result practiceGenerate(HashMap<String, String> info);
}
