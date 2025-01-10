package com.schall.jyyxbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.schall.jyyx.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.schall.jyyx.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.vo.QuestionSubmitVO;


/**
* @author Alter
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-10-15 13:44:41
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest todo 题目提交信息
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);


    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest );



    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    /**
     * 更新题目提交的智能分析结果
     *
     * @param questionSubmitId 题目提交ID
     * @param analysisData     智能分析结果
     * @return 是否更新成功
     */
    boolean updateAnalysisData(long questionSubmitId, String analysisData);

    /**
     * 根据题目提交ID获取智能分析结果
     *
     * @param questionSubmitId 题目提交ID
     * @return 智能分析结果
     */
    String getAnalysisDataById(long questionSubmitId);


    /**
     * 更新个性化反馈和教学指导
     *
     * @param questionSubmitId     题目提交 ID
     * @param personalizedFeedback 个性化学习反馈
     * @param teachingGuidance     教学指导建议
     * @return 是否更新成功
     */
    boolean updateFeedbackAndGuidance(long questionSubmitId, String personalizedFeedback, String teachingGuidance);
    /**
     * 获取个性化反馈
     *
     * @param questionSubmitId 提交的题目 ID
     * @return 个性化学习反馈
     */
    String getPersonalizedFeedback(long questionSubmitId);

    /**
     * 获取教学指导
     *
     * @param questionSubmitId 提交的题目 ID
     * @return 教学指导建议
     */
    String getTeachingGuidance(long questionSubmitId);
}
