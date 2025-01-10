package com.schall.jyyx.model.dto.questionsubmit;

import lombok.*;
import com.schall.jyyxblackendcommon.common.PageRequest;

import java.io.Serializable;
/**
 * 创建请求
 */
@EqualsAndHashCode(callSuper = true)
@Data



public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户 id
     */
    private Long user_id;

    private static final long serialVersionUID = 1L;
}