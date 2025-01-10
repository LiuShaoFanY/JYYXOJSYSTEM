package com.schall.jyyx.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交
 * @TableName question_submit
 */


@TableName(value ="question_submit")
@Data
public class QuestionSubmit implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)//题目id，每创建一次自动自己序号，但是容易被爬虫爬， todo 需要修改为ASSIGN_ID
    private Long id;


    private String analysisData;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;

    /**
     * 判题信息(json 对象)
     */
    private String judgeInfo;

    /**
     * 判题状态 (0 - 待判题,1 - 判题中,2 - 成功,3 - 失败)
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long user_id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private String personalizedFeedback; // 个性化学习反馈
    private String teachingGuidance;     // 教学指导建议
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}