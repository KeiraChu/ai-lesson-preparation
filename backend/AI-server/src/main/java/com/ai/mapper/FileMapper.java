package com.ai.mapper;

import com.ai.entity.LessonPlan;
import com.ai.entity.MyFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;

/**
 * @author ：褚婧雯
 * @date ：2025/4/12 19:28
 * @description ：
 */
public interface FileMapper extends BaseMapper<MyFile> {
    Page<MyFile> pageQuery(String id);
}
