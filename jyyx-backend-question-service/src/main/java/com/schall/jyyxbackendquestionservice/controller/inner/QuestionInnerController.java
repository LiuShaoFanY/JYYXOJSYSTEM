package com.schall.jyyxbackendquestionservice.controller.inner;

import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyxbackendquestionservice.service.QuestionService;
import com.schall.jyyxbackendquestionservice.service.QuestionSubmitService;
import com.schall.jyyxbackendserviceclient.service.QuestionFeignClient;
import com.schall.jyyxblackendcommon.common.BaseResponse;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.common.ResultUtils;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 内部调用的题目和题目提交相关接口控制器
 * 实现QuestionFeignClient接口，供其他服务通过Feign调用
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    private static final Logger log = LoggerFactory.getLogger(QuestionInnerController.class);

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    /**
     * 根据题目提交ID获取题目提交信息
     *
     * @param questionSubmitId 题目提交ID
     * @return 题目提交信息
     */
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        log.info("根据ID获取题目提交信息，questionSubmitId = {}", questionSubmitId);
        return questionSubmitService.getById(questionSubmitId);
    }

    /**
     * 根据题目ID获取题目信息
     *
     * @param questionId 题目ID
     * @return 题目信息
     */
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        log.info("根据ID获取题目信息，questionId = {}", questionId);
        return questionService.getById(questionId);
    }

    /**
     * 更新题目提交信息
     *
     * @param questionSubmit 题目提交信息
     * @return 是否更新成功
     */
    @PostMapping("/question-submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        log.info("更新题目提交信息，questionSubmit = {}", questionSubmit);
        return questionSubmitService.updateById(questionSubmit);
    }

    /**
     * 更新题目提交的分析数据
     *
     * @param questionSubmitId 题目提交ID
     * @param analysisData     分析数据
     * @return 是否更新成功
     */
    @PostMapping("/question_submit/updateAnalysisData")
    public boolean updateAnalysisData(@RequestParam("questionSubmitId") long questionSubmitId, @RequestParam("analysisData") String analysisData) {
        log.info("更新题目提交的分析数据，questionSubmitId = {}, analysisData = {}", questionSubmitId, analysisData);
        return questionSubmitService.updateAnalysisData(questionSubmitId, analysisData);
    }

    /**
     * 获取题目提交的分析数据
     *
     * @param questionSubmitId 题目提交ID
     * @return 分析数据
     */
    @GetMapping("/question_submit/getAnalysisData")
    public String getAnalysisData(@RequestParam("questionSubmitId") long questionSubmitId) {
        log.info("获取题目提交的分析数据，questionSubmitId = {}", questionSubmitId);
        return questionSubmitService.getAnalysisDataById(questionSubmitId);
    }

    /**
     * 更新题目的通过次数
     *
     * @param questionId 题目ID
     * @param increment  增量
     * @return 是否更新成功
     */
    @PostMapping("/question/updateAcceptedNum")
    public boolean updateAcceptedNum(@RequestParam("questionId") long questionId, @RequestParam("increment") int increment) {
        log.info("更新题目的通过次数，questionId = {}, increment = {}", questionId, increment);
        return questionService.updateAcceptedNum(questionId, increment);
    }

    /**
     * 更新题目提交的个性化反馈和教学指导
     *
     * @param questionSubmitId     题目提交ID
     * @param personalizedFeedback 个性化反馈
     * @param teachingGuidance     教学指导
     * @return 是否更新成功
     */
    @PostMapping("/question_submit/updateFeedbackAndGuidance")
    public boolean updateFeedbackAndGuidance(@RequestParam("questionSubmitId") long questionSubmitId,
                                             @RequestParam("personalizedFeedback") String personalizedFeedback,
                                             @RequestParam("teachingGuidance") String teachingGuidance) {
        log.info("更新题目提交的个性化反馈和教学指导，questionSubmitId = {}, feedback = {}, guidance = {}", questionSubmitId, personalizedFeedback, teachingGuidance);
        return questionSubmitService.updateFeedbackAndGuidance(questionSubmitId, personalizedFeedback, teachingGuidance);
    }

    /**
     * 获取题目提交的个性化反馈和教学指导
     *
     * @param questionSubmitId 题目提交ID
     * @return 包含个性化反馈和教学指导的Map
     */
    @GetMapping("/getFeedbackAndGuidance")
    public BaseResponse<Map<String, String>> getFeedbackAndGuidance(@RequestParam long questionSubmitId) {
        try {
            log.info("获取题目提交的个性化反馈和教学指导，questionSubmitId = {}", questionSubmitId);
            String personalizedFeedback = questionSubmitService.getPersonalizedFeedback(questionSubmitId);
            String teachingGuidance = questionSubmitService.getTeachingGuidance(questionSubmitId);
            Map<String, String> result = new HashMap<>();
            result.put("personalizedFeedback", personalizedFeedback);
            result.put("teachingGuidance", teachingGuidance);
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            log.error("获取个性化反馈和教学指导失败，questionSubmitId = {}", questionSubmitId, e);
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("系统异常，questionSubmitId = {}", questionSubmitId, e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}