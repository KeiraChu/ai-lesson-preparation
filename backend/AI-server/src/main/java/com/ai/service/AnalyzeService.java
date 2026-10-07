package com.ai.service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/13 16:21
 * @description ：
 */
public interface AnalyzeService {
    /**
     * 学情分析
     * @param name
     * @param id
     * @return
     */
    ArrayList<HashMap> analyzeTest(String name, String id);
}
