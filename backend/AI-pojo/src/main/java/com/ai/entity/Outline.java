package com.ai.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ：褚婧雯
 * @date ：2025/4/9 19:35
 * @description ：大纲实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Outline {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; // 主键id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userid; // 用户id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long pptid; // pptId

    private String outline; // 大纲内容

    private Integer deleted; // 是否删除(1未删除；0已删除)，使用Boolean对象以允许null值

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime; // 创建时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime; // 更新时间
}
