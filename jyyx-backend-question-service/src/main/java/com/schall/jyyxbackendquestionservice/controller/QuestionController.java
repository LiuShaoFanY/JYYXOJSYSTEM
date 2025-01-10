package com.schall.jyyxbackendquestionservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.schall.jyyx.model.dto.question.JudgeCase;
import com.schall.jyyx.model.dto.question.JudgeConfig;
import com.schall.jyyx.model.dto.question.QuestionAddRequest;
import com.schall.jyyx.model.dto.question.QuestionEditRequest;
import com.schall.jyyx.model.dto.question.QuestionQueryRequest;
import com.schall.jyyx.model.dto.question.QuestionUpdateRequest;
import com.schall.jyyx.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.schall.jyyx.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.schall.jyyx.model.entity.Question;
import com.schall.jyyx.model.entity.QuestionSubmit;
import com.schall.jyyx.model.entity.User;
import com.schall.jyyx.model.enums.UserRoleEnum;
import com.schall.jyyx.model.vo.QuestionSubmitVO;
import com.schall.jyyx.model.vo.QuestionVO;
import com.schall.jyyxbackendquestionservice.service.QuestionService;
import com.schall.jyyxbackendquestionservice.service.QuestionSubmitService;
import com.schall.jyyxbackendserviceclient.service.UserFeignClient;
import com.schall.jyyxblackendcommon.annotation.AuthCheck;
import com.schall.jyyxblackendcommon.common.BaseResponse;
import com.schall.jyyxblackendcommon.common.DeleteRequest;
import com.schall.jyyxblackendcommon.common.ErrorCode;
import com.schall.jyyxblackendcommon.common.ResultUtils;
import com.schall.jyyxblackendcommon.exception.BusinessException;
import com.schall.jyyxblackendcommon.exception.ThrowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 题目相关接口控制器
 * 提供题目的增删改查、提交、反馈等功能
 */
@RestController
@RequestMapping("/")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;

    private static final Gson GSON = new Gson();

    /**
     * 添加题目
     *
     * @param questionAddRequest 题目添加请求
     * @param request            HTTP请求
     * @return 新题目的ID
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCases = questionAddRequest.getJudgeCase();
        if (judgeCases != null) {
            question.setJudgeCase(GSON.toJson(judgeCases));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        question.setUser_id(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除题目
     *
     * @param deleteRequest 删除请求
     * @param request       HTTP请求
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldQuestion.getUser_id().equals(user.getId()) &&
                !userFeignClient.isAdministrator(user) &&
                !userFeignClient.isTeacher(user)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 更新题目
     *
     * @param questionUpdateRequest 题目更新请求
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = {"administrator"})
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCases = questionUpdateRequest.getJudgeCase();
        if (judgeCases != null) {
            question.setJudgeCase(GSON.toJson(judgeCases));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取题目
     *
     * @param id      题目ID
     * @param request HTTP请求
     * @return 题目信息
     */
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(@RequestParam long id, HttpServletRequest request) {
        if (id <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        if (!question.getUser_id().equals(loginUser.getId()) &&
                !UserRoleEnum.ADMINISTRATOR.equals(loginUser.getUserRole()) &&
                !UserRoleEnum.TEACHER.equals(loginUser.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据ID获取题目视图对象
     *
     * @param id      题目ID
     * @param request HTTP请求
     * @return 题目视图对象
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(@RequestParam long id, HttpServletRequest request) {
        try {
            if (id <= 0L) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
            Question question = questionService.getById(id);
            if (question == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            QuestionVO questionVO = questionService.getQuestionVO(question, request);
            return ResultUtils.success(questionVO);
        } catch (BusinessException e) {
            log.error("获取题目视图对象失败", e);
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("系统异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 分页获取题目视图对象列表
     *
     * @param questionQueryRequest 题目查询请求
     * @param request              HTTP请求
     * @return 分页的题目视图对象列表
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20L, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size), questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取当前用户的题目视图对象列表
     *
     * @param questionQueryRequest 题目查询请求
     * @param request              HTTP请求
     * @return 分页的题目视图对象列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionQueryRequest.setUser_id(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        ThrowUtils.throwIf(size > 20L, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size), questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取题目列表（管理员权限）
     *
     * @param questionQueryRequest 题目查询请求
     * @param request              HTTP请求
     * @return 分页的题目列表
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = {"administrator"})
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size), questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }

    /**
     * 编辑题目
     *
     * @param questionEditRequest 题目编辑请求
     * @param request             HTTP请求
     * @return 是否编辑成功
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCases = questionEditRequest.getJudgeCase();
        if (judgeCases != null) {
            question.setJudgeCase(GSON.toJson(judgeCases));
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        if (!oldQuestion.getUser_id().equals(loginUser.getId()) && !userFeignClient.isAdministrator(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest 题目提交请求
     * @param request                  HTTP请求
     * @return 题目提交ID
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest, HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取题目提交记录
     *
     * @param questionSubmitQueryRequest 题目提交查询请求
     * @param request                    HTTP请求
     * @return 分页的题目提交记录
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size), questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        User loginUser = userFeignClient.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }

    /**
     * 更新个性化反馈和教学指导
     *
     * @param questionSubmitId     题目提交ID
     * @param personalizedFeedback 个性化反馈
     * @param teachingGuidance     教学指导
     * @return 是否更新成功
     */
    @PostMapping("/updateFeedbackAndGuidance")
    public BaseResponse<Boolean> updateFeedbackAndGuidance(@RequestParam long questionSubmitId,
                                                           @RequestParam String personalizedFeedback,
                                                           @RequestParam String teachingGuidance) {
        try {
            String decodedFeedback = URLDecoder.decode(personalizedFeedback, StandardCharsets.UTF_8.toString());
            String decodedGuidance = URLDecoder.decode(teachingGuidance, StandardCharsets.UTF_8.toString());
            boolean result = questionSubmitService.updateFeedbackAndGuidance(questionSubmitId, decodedFeedback, decodedGuidance);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新反馈和指导失败");
            }
            return ResultUtils.success(true);
        } catch (UnsupportedEncodingException e) {
            log.error("URL解码失败", e);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "URL解码失败");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("系统异常", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常");
        }
    }

    /**
     * 获取个性化反馈和教学指导
     *
     * @param questionSubmitId 题目提交ID
     * @return 包含个性化反馈和教学指导的Map
     */
    @GetMapping("/getFeedbackAndGuidance")
    public BaseResponse<Map<String, String>> getFeedbackAndGuidance(@RequestParam long questionSubmitId) {
        try {
            String personalizedFeedback = questionSubmitService.getPersonalizedFeedback(questionSubmitId);
            String teachingGuidance = questionSubmitService.getTeachingGuidance(questionSubmitId);
            Map<String, String> result = new HashMap<>();
            result.put("personalizedFeedback", personalizedFeedback);
            result.put("teachingGuidance", teachingGuidance);
            return ResultUtils.success(result);
        } catch (BusinessException e) {
            log.error("获取反馈和指导失败", e);
            return ResultUtils.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("系统异常", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }
}