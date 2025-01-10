package com.schall.jyyx.model.dto.question;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.schall.jyyxblackendcommon.common.PageRequest;


import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 * @author
 * @from
 */

@EqualsAndHashCode(callSuper = true)
@Data

public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 创建用户 id
     */
    private Long user_id;

    private static final long serialVersionUID = 1L;


}