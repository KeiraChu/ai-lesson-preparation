package com.ai.mapper;

import com.ai.entity.LessonPlan;
import com.ai.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;

/**
 * @author ：褚婧雯
 * @date ：2025/4/7 21:11
 * @description ：针对表【lesson_plan(用户教案表)】的数据库操作Mapper
 */
public interface LessonPlanMapper extends BaseMapper<LessonPlan> {
    /**
     * 分页查询教案
     * @param userid
     * @return
     */
    Page<LessonPlan> pageQuery(String userid);
}
