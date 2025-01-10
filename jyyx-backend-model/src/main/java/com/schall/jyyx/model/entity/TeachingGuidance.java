package com.schall.jyyx.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("teachingGuidance")
public class TeachingGuidance {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionSubmitId; // 题目提交ID

    private String teachingGuidance; // 教学指导建议
}