package com.ai.controller;

import com.ai.result.Result;
import com.ai.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/11 21:00
 * @description ：练习题controller
 */
@RestController
@RequestMapping("/practice")
public class PracticeController {
    @Autowired
    private PracticeService practiceService;
    /**
     * 根据教案生成练习题
     * @param info
     * @return
     */
    @PostMapping()
    public Result practiceGenerate(@RequestBody HashMap<String,String> info){
        return practiceService.practiceGenerate(info);
    }
}
