package com.ai.mapper;

import com.ai.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author ：褚婧雯
 * @date ：2025/3/25 14:59
 * @description ：针对表【user(用户表)】的数据库操作Mapper
 */
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user where username = #{username}")
    User getUser(String username);

    @Select("select * from user")
    List<User> getAllUser();
}
