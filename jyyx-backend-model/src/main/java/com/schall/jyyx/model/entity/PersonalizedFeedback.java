package com.schall.jyyx.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("personalizedFeedback")
public class PersonalizedFeedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionSubmitId; // 题目提交ID

    private String personalizedFeedback; // 个性化学习反馈
}