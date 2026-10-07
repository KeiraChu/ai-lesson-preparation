package com.ai.controller;

import com.ai.entity.LessonPlan;
import com.ai.result.PageResult;
import com.ai.result.Result;
import com.ai.service.TextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/7 14:58
 * @description ：教案生成
 */
@RestController
@RequestMapping("/text")
public class TextController {

    @Autowired
    private TextService textService;


    /**
     * 教案生成
     * @param prompt
     * @return
     */
    @PostMapping
    public Result textGen(@RequestBody String prompt) {
        return textService.textGen(prompt);
    }

    /**
     * 教案生成，流式响应
     * @param prompt
     * @return
     */
    @PostMapping(value = "/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Result> textGenStream(@RequestBody String prompt){
        return textService.textGenStream(prompt);
    }


    /**
     * 保存教案
     * @param info
     * @return
     */
    @PostMapping("/lesson")
    public Result lessonSave(@RequestBody HashMap<String,String> info) {
        return textService.lessonSave(info);
    }

    /**
     * 删除教案
     * @param info
     * @return
     */
    @DeleteMapping("/lesson")
    public Result lessonDelete(@RequestBody HashMap<String,String> info) {
        return textService.lessonDelete(info);
    }

    /**
     * 分页查询教案
     * @param info
     * @return
     */
    @PostMapping("/lessonlist")
    public Result<PageResult> lessonList(@RequestBody HashMap<String,String> info) {
        PageResult pageResult = textService.pageQuery(info);
        return Result.success(pageResult);
    }

    /**
     * 修改教案
     * @return
     */
    @PutMapping("/lesson")
    public Result updateLesson(@RequestBody HashMap<String,String> info) {
        return textService.updateLesson(info);
    }

    /**
     * 根据教案id查询教案
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public Result<LessonPlan> getById(@RequestParam String id){
        return textService.getById(id);
    }
}
