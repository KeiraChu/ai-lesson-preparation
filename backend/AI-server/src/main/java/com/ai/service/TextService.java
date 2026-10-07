package com.ai.service;

import com.ai.entity.LessonPlan;
import com.ai.result.PageResult;
import com.ai.result.Result;
import reactor.core.publisher.Flux;

import java.util.HashMap;

/**
 * @author ：褚婧雯
 * @date ：2025/4/7 15:30
 * @description ：教案生成
 */
public interface TextService {

    Result textGen(String prompt);

    /**
     * 保存教案
     * @param info
     * @return
     */
    Result lessonSave(HashMap<String, String> info);

    /**
     * 删除教案
     * @param info
     * @return
     */
    Result lessonDelete(HashMap<String, String> info);

    /**
     * 分页查询教案
     * @param info
     * @return
     */
    PageResult pageQuery(HashMap<String, String> info);

    /**
     * 修改教案
     * @return
     */
    Result updateLesson(HashMap<String, String> info);

    /**
     * 根据教案id查询教案
     * @param id
     * @return
     */
    Result<LessonPlan> getById(String id);

    /**
     * 教案生成，流式响应
     * @param prompt
     * @return
     */
    Flux<Result> textGenStream(String prompt);
}
