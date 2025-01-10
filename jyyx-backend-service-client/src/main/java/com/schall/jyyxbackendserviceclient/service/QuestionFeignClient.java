package com.schall.jyyxbackendserviceclient.service;

import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 题目服务Feign客户端接口
 * 用于通过Feign调用题目服务的内部接口
 */
@FeignClient(name = "jyyx-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    /**
     * 根据题目提交ID获取题目提交信息
     *
     * @param questionSubmitId 题目提交ID
     * @return 题目提交信息
     */
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId);

    /**
     * 根据题目ID获取题目信息
     *
     * @param questionId 题目ID
     * @return 题目信息
     */
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    /**
     * 更新题目提交信息
     *
     * @param questionSubmit 题目提交信息
     * @return 是否更新成功
     */
    @PostMapping("/question-submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

    /**
     * 更新题目提交的分析数据
     *
     * @param questionSubmitId 题目提交ID
     * @param analysisData     分析数据
     * @return 是否更新成功
     */
    @PostMapping("/question_submit/updateAnalysisData")
    boolean updateAnalysisData(@RequestParam("questionSubmitId") long questionSubmitId, @RequestParam("analysisData") String analysisData);

    /**
     * 获取题目提交的分析数据
     *
     * @param questionSubmitId 题目提交ID
     * @return 分析数据
     */
    @GetMapping("/question_submit/getAnalysisData")
    String getAnalysisData(@RequestParam("questionSubmitId") long questionSubmitId);

    /**
     * 更新题目的通过次数
     *
     * @param questionId 题目ID
     * @param increment  增量
     * @return 是否更新成功
     */
    @PostMapping("/question/updateAcceptedNum")
    boolean updateAcceptedNum(@RequestParam("questionId") long questionId, @RequestParam("increment") int increment);

    /**
     * 更新题目提交的个性化反馈和教学指导
     *
     * @param questionSubmitId     题目提交ID
     * @param personalizedFeedback 个性化反馈
     * @param teachingGuidance     教学指导
     * @return 是否更新成功
     */
    @PostMapping("/question_submit/updateFeedbackAndGuidance")
    boolean updateFeedbackAndGuidance(@RequestParam("questionSubmitId") long questionSubmitId,
                                      @RequestParam("personalizedFeedback") String personalizedFeedback,
                                      @RequestParam("teachingGuidance") String teachingGuidance);
}