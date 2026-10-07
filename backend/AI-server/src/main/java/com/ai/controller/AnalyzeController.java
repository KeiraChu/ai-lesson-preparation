package com.ai.controller;

import com.ai.service.AnalyzeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/13 16:17
 * @description ：学情分析
 */
@RestController
@RequestMapping("/analyze")
public class AnalyzeController {

    @Autowired
    private AnalyzeService analyzeService;

    /**
     * 学情分析
     * @param name
     * @param id
     * @return
     */
    @GetMapping
    public ArrayList<HashMap> analyzeTest(@RequestParam String name, @RequestParam String id){
        return analyzeService.analyzeTest(name,id);
    }
}
