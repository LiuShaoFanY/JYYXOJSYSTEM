package com.schall.jyyxbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schall.jyyx.model.dto.question.QuestionQueryRequest;
import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Alter
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-10-15 13:42:45
*/
public interface QuestionService extends IService<Question> {
    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);



    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 更新题目的提交数
     *
     * @param questionId 题目ID
     * @param increment  增加的提交数
     * @return 是否更新成功
     */
    boolean updateSubmitNum(long questionId, int increment);

    /**
     * 更新题目的通过数
     *
     * @param questionId 题目ID
     * @param increment  增加的通过数
     * @return 是否更新成功
     */
    boolean updateAcceptedNum(long questionId, int increment);

    /**
     * 更新个性化反馈和教学指导。
     *
     * @param questionSubmitId     提交的题目 ID
     * @param personalizedFeedback 个性化学习反馈
     * @param teachingGuidance     教学指导建议
     * @return 是否更新成功
     */
    boolean updateFeedbackAndGuidance(long questionSubmitId, String personalizedFeedback, String teachingGuidance);
}
