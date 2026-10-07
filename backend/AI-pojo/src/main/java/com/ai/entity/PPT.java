package com.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @date ：2025/4/9 19:33
 * @description ：ppt记录表实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("ppt")
public class PPT {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id; // 主键id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userid; // 用户id

    private String coverImgSrc; // 封面图

    private String title; // 标题

    private String subTitle; // 副标题

    private String sid; // ppt查询id

    private Integer deleted; // 是否删除(1未删除；0已删除)

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime; // 创建时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime; // 更新时间
}
